package com.pigeon_stargram.sns_clone.service.reply.implV2;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.pigeon_stargram.sns_clone.domain.reply.ReplyFactory.*;

/**
 * ReplyWriteBackService는 Redis 캐시와 데이터베이스 간의 데이터 동기화 작업을 처리하는 서비스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReplyWriteBackService {

    private final RedisService redisService; // Redis 캐시 서비스
    private final UserService userService; // 사용자 서비스
    private final ReplyServiceV2 replyService; // 답글 서비스
    private final ReplyLikeRepository replyLikeRepository; // 답글 좋아요 저장소

    /**
     * Redis 캐시에서 좋아요 사용자 ID를 가져와 데이터베이스와 동기화합니다.
     *
     * @param key Redis 캐시의 키
     */
    public void syncReplyLikeUserIds(String key) {
        // Redis 캐시에서 답글 ID를 추출합니다.
        Long replyId = RedisUtil.parseSuffix(key);

        // Redis 캐시에서 좋아요 사용자 ID 목록을 가져옵니다. 더미 데이터를 제외합니다.
        List<Long> cacheReplyLikeUserIds = redisService.getSetAsLongListExcludeDummy(key);

        // 데이터베이스에 존재하지 않는 사용자 ID에 대해 좋아요 정보를 저장합니다.
        cacheReplyLikeUserIds.stream()
                .filter(userId -> !replyLikeRepository.existsByUserIdAndReplyId(userId, replyId))
                .forEach(userId -> saveReplyLike(userId, replyId));
    }

    /**
     * 주어진 사용자 ID와 답글 ID에 대해 좋아요 정보를 저장합니다.
     *
     * @param userId  좋아요를 누른 사용자 ID
     * @param replyId 답글 ID
     */
    private void saveReplyLike(Long userId, Long replyId) {
        ReplyLike replyLike = getReplyLike(userId, replyId);
        replyLikeRepository.save(replyLike);
    }

    /**
     * 사용자 ID와 답글 ID를 기반으로 ReplyLike 객체를 생성합니다.
     *
     * @param replyLikeUserId 사용자 ID
     * @param replyId 답글 ID
     * @return 생성된 ReplyLike 객체
     */
    private ReplyLike getReplyLike(Long replyLikeUserId, Long replyId) {
        User replyLikeUser = userService.getUserById(replyLikeUserId); // 사용자 정보를 가져옵니다.
        Reply reply = replyService.findById(replyId); // 답글 정보를 가져옵니다.

        // ReplyDtoConverter를 통해 ReplyLike 객체를 생성합니다.
        return createReplyLike(replyLikeUser, reply);
    }
}
