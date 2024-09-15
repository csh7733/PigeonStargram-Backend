package com.pigeon_stargram.sns_clone.service.reply.implV2;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.REPLY_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

/**
 * ReplyCrudServiceImpl는 ReplyCrudService 인터페이스를 구현하며, 답글과 관련된 CRUD 작업을 수행합니다.
 * 이 클래스는 Redis 캐시를 활용하여 성능을 최적화합니다.
 */
// Value     | Structure | Key
// --------- | --------- | -------------------
// reply     | String    | REPLY               JSON 직렬화된 Comment객체
// replyId   | Set       | ALL_REPLY_IDS       게시물의 모든 답글 ID
// replyId   | Set       | ALL_REPLY_IDS       답글의 모든 답글 ID
// userId    | Set       | REPLY_LIKE_USER_IDS 답글을 좋아하는 사용자 ID
@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyCrudServiceV2 implements ReplyCrudService {

    private final RedisService redisService;

    private final ReplyRepository repository;

    @Cacheable(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    @Transactional(readOnly = true)
    @Override
    public Reply findById(Long replyId) {
        return repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findReplyIdByCommentId(Long commentId) {
        // 캐시 키 생성
        String cacheKey = cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());

        // 캐시에서 답글 ID 리스트 조회
        if (redisService.hasKey(cacheKey)) {
            return redisService.getSetAsLongListExcludeDummy(cacheKey);
        }

        // 데이터베이스에서 답글 ID 리스트 조회
        List<Long> replyIds = getReplyIdsFromRepository(commentId);

        // 조회된 데이터를 캐시에 저장
        return redisService.cacheListToSetWithDummy(replyIds, cacheKey, ONE_DAY_TTL);
    }

    /**
     * 주어진 답글 객체를 데이터베이스에 저장하고, 관련 캐시를 갱신합니다.
     *
     * @param reply 저장할 답글 객체
     * @return 저장된 답글 객체
     */
    @CachePut(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #reply.id")
    @Transactional
    @Override
    public Reply save(Reply reply) {
        // 데이터베이스에 답글 저장
        Reply save = repository.save(reply);

        // 댓글 ID를 이용한 캐시 키 생성
        Long commentId = reply.getComment().getId();

        // 모든 답글 ID 리스트 캐시 갱신
        String allReplyIds = cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(allReplyIds)) {
            redisService.addToSet(allReplyIds, reply.getId(), ONE_DAY_TTL);
        }

        // 최신 답글 ID 리스트 캐시 갱신
        String recentReplyIds = cacheKeyGenerator(RECENT_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(recentReplyIds)) {
            redisService.addToSet(recentReplyIds, reply.getId(), ONE_DAY_TTL);
        }

        return save;
    }

    /**
     * 주어진 답글 ID와 새로운 내용을 바탕으로 답글을 수정합니다.
     * 수정된 답글 정보를 캐시에 갱신합니다.
     *
     * @param replyId 수정할 답글의 ID
     * @param newContent 새로운 답글 내용
     * @return 수정된 답글 객체
     * @throws ReplyNotFoundException 답글을 찾을 수 없을 때 발생
     */
    @CachePut(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    @Transactional
    @Override
    public Reply edit(Long replyId, String newContent) {
        // 데이터베이스에서 답글 조회
        Reply reply = repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
        // 답글 내용 수정
        reply.editContent(newContent);

        return reply;
    }

    /**
     * 주어진 답글 ID에 해당하는 답글을 데이터베이스에서 삭제합니다.
     * 삭제 후 관련 캐시에서 답글 ID를 제거합니다.
     *
     * @param replyId 삭제할 답글의 ID
     */
    @CacheEvict(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    @Transactional
    @Override
    public void deleteById(Long replyId) {
        // 데이터베이스에서 답글 삭제
        Long commentId = findById(replyId).getComment().getId();
        repository.deleteById(replyId);

        // 캐시에서 답글 ID 제거
        String allReplyIds = cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(allReplyIds)) {
            redisService.removeFromSet(allReplyIds, replyId);
        }
    }

    /**
     * 데이터베이스에서 주어진 댓글 ID에 해당하는 답글 ID 리스트를 조회합니다.
     *
     * @param commentId 댓글의 ID
     * @return 댓글에 달린 답글 ID 리스트
     */
    private List<Long> getReplyIdsFromRepository(Long commentId) {
        return repository.findByCommentId(commentId).stream()
                .map(Reply::getId)
                .collect(Collectors.toList());
    }
}
