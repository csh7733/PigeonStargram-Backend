package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

/**
 * ReplyLikeCrudServiceV2는 답글에 대한 좋아요 정보를 관리하는 서비스입니다.
 * Redis를 이용하여 캐싱 및 성능 최적화를 수행하며, 데이터베이스와의 동기화 작업을 처리합니다.
 */
// Value  | Structure | Key
// -----  | --------- | --------------------
// userId | Set       | REPLY_LIKE_USER_IDS
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReplyLikeCrudServiceV2 implements ReplyLikeCrudService {

    private final RedisService redisService;
    private final ReplyLikeRepository repository;

    @Override
    public void toggleLike(Long userId, Long replyId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        // 캐시 히트 시
        if (redisService.hasKey(cacheKey)) {

            // 캐시에서 좋아요 정보 확인
            if (redisService.isMemberOfSet(cacheKey, userId)) {
                // 좋아요가 존재하면 제거하고, 데이터베이스에서 삭제
                redisService.removeFromSet(cacheKey, userId);
                repository.deleteByUserIdAndReplyId(userId, replyId);
            } else {
                // 좋아요가 존재하지 않으면 추가하고, 캐시에 저장
                redisService.addToSet(cacheKey, userId, ONE_DAY_TTL);
                redisService.pushToWriteBackSortedSet(cacheKey);
            }

            return;
        }

        // 캐시 미스 시
        List<Long> replyLikeUserIds = getReplyLikeUserIdsFromRepositoryWithDummy(replyId);

        // 좋아요 정보 토글
        if (replyLikeUserIds.contains(userId)) {
            replyLikeUserIds.remove(userId);
            repository.deleteByUserIdAndReplyId(userId, replyId);
        } else {
            replyLikeUserIds.add(userId);
            redisService.pushToWriteBackSortedSet(cacheKey);
        }

        // 캐시에 좋아요 사용자 ID 리스트 저장
        redisService.addAllToSet(cacheKey, replyLikeUserIds, ONE_DAY_TTL);
    }

    @Override
    public Integer countByReplyId(Long replyId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        // 캐시에서 좋아요 수 조회
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetSize(cacheKey).intValue() - 1;
        }

        // 데이터베이스에서 좋아요 사용자 ID 리스트 조회
        List<Long> replyLikeUserIds = getReplyLikeUserIdsFromRepositoryWithDummy(replyId);

        // 캐시에 좋아요 사용자 ID 리스트 저장
        redisService.addAllToSet(cacheKey, replyLikeUserIds, ONE_DAY_TTL);

        return replyLikeUserIds.size() - 1;
    }

    @Override
    public List<Long> getReplyLikeUserIds(Long replyId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());

        // 캐시에서 사용자 ID 리스트 조회
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 데이터베이스에서 좋아요 사용자 ID 리스트 조회
        List<Long> replyLikeUserIds = getReplyLikeUserIdsFromRepository(replyId);

        // 캐시에 좋아요 사용자 ID 리스트 저장
        return redisService.cacheListToSetWithDummy(replyLikeUserIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 데이터베이스에서 답글 ID에 대한 좋아요 사용자 ID 리스트를 조회합니다.
     * 더미 데이터(0L)를 추가하여 캐시 저장을 위한 리스트를 생성합니다.
     *
     * @param replyId 답글 ID
     * @return 좋아요 사용자 ID 리스트 (더미 데이터 포함)
     */
    private List<Long> getReplyLikeUserIdsFromRepositoryWithDummy(Long replyId) {
        List<Long> replyLikeUserIds = repository.findByReplyId(replyId).stream()
                .map(ReplyLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
        // 비어있는 set을 캐시하기 위한 더미 데이터 추가
        replyLikeUserIds.add(0L);
        return replyLikeUserIds;
    }

    /**
     * 데이터베이스에서 답글 ID에 대한 좋아요 사용자 ID 리스트를 조회합니다.
     * 더미 데이터는 포함하지 않습니다.
     *
     * @param replyId 답글 ID
     * @return 좋아요 사용자 ID 리스트
     */
    private List<Long> getReplyLikeUserIdsFromRepository(Long replyId) {
        return repository.findByReplyId(replyId).stream()
                .map(ReplyLike::getUser)
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
