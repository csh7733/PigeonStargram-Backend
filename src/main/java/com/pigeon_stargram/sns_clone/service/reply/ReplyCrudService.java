package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.POST_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.REPLY_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReplyCrudService {

    private final RedisService redisService;

    private final ReplyRepository repository;

    @Cacheable(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    public Reply findById(Long replyId) {
        return repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
    }

    public List<Long> findReplyIdByCommentId(Long commentId) {
        String cacheKey = cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findReplyIdsByUserId = {} 캐시 히트", commentId);

            return redisService.getSet(cacheKey).stream()
                    .filter(replyId -> !replyId.equals(0))
                    .map(replyId -> Long.valueOf((Integer) replyId))
                    .collect(Collectors.toList());
        }

        log.info("findReplyIdsByUserId = {} 캐시 미스", commentId);

        List<Long> replyIds = repository.findByCommentId(commentId).stream()
                .map(Reply::getId)
                .collect(Collectors.toList());

        replyIds.add(0L);
        redisService.addAllToSet(cacheKey, replyIds);

        replyIds.remove(0L);
        return replyIds;
    }

    @CachePut(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #reply.id")
    public Reply save(Reply reply) {
        Reply save = repository.save(reply);

        Long commentId = reply.getComment().getId();

        String allReplyIds =
                cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(allReplyIds)) {
            log.info("reply 저장후 commentId에 대한 모든 replyId 캐시 저장 commentId = {}", commentId);
            redisService.addToSet(allReplyIds, reply.getId());
        }

        String recentReplyIds =
                cacheKeyGenerator(RECENT_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(recentReplyIds)) {
            log.info("reply 저장후 commentId에 대한 최근 replyId 캐시 저장 commentId = {}", commentId);
            redisService.addToSet(recentReplyIds, reply.getId());
        }

        return save;
    }

    @CachePut(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    public Reply edit(Long replyId,
                     String newContent) {
        // 영속화된 reply
        Reply reply = repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
        reply.modify(newContent);

        return reply;
    }

    @CacheEvict(value = REPLY,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).REPLY_ID + '_' + #replyId")
    public void deleteById(Long replyId) {
        Long commentId = findById(replyId).getComment().getId();
        repository.deleteById(replyId);

        String allReplyIds =
                cacheKeyGenerator(ALL_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(allReplyIds)) {
            log.info("reply 삭제후 commentId에 대한 모든 replyId 캐시 삭제 commentId = {}", commentId);
            redisService.removeFromSet(allReplyIds, replyId);
        }

        String recentReplyIds =
                cacheKeyGenerator(RECENT_REPLY_IDS, COMMENT_ID, commentId.toString());
        if (redisService.hasKey(recentReplyIds)) {
            log.info("reply 삭제후 commentId에 대한 최근 replyId 캐시 삭제 commentId = {}", commentId);
            redisService.removeFromSet(recentReplyIds, replyId);
        }

        String replyLikeUserIds =
                cacheKeyGenerator(REPLY_LIKE_USER_IDS, REPLY_ID, replyId.toString());
        if (redisService.hasKey(replyLikeUserIds)) {
            log.info("reply 삭제후 replyId에 대한 replyLikeUserIds 캐시 삭제 replyId = {}", replyId);
            redisService.removeSet(replyLikeUserIds);
        }
    }

}
