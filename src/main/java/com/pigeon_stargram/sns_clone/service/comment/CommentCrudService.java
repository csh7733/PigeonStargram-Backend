package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
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
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.COMMENT_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentCrudService {

    private final RedisService redisService;

    private final CommentRepository repository;

    @Cacheable(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment findById(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
    }

    public List<Long> findCommentIdByPostId(Long postId) {
        String cacheKey = cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());

        if (redisService.hasKey(cacheKey)) {
            log.info("findCommentIdsByPostId = {} 캐시 히트", postId);

            return redisService.getSet(cacheKey).stream()
                    .map(commentId -> Long.valueOf((Integer) commentId))
                    .collect(Collectors.toList());
        }

        log.info("findCommentIdsByPostId = {} 캐시 미스", postId);
        List<Long> postIds = repository.findByPostId(postId).stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            redisService.addToSet(cacheKey, "dummy");
            redisService.removeFromSet(cacheKey, "dummy");
        } else {
            redisService.addAllToSet(cacheKey, postIds);
        }

        return postIds;
    }

    @CachePut(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #comment.id")
    public Comment save(Comment comment) {
        Comment save = repository.save(comment);

        Long postId = comment.getPost().getId();

        String allPostIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allPostIds)) {
            log.info("comment 저장후 postId에 대한 모든 commentId 캐시 저장 commentId = {}", postId);
            redisService.addToSet(allPostIds, comment.getId());
        }

        String recentPostIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(recentPostIds)) {
            log.info("comment 저장후 postId에 대한 최근 commentId 캐시 저장 commentId = {}", postId);
            redisService.addToSet(recentPostIds, comment.getId());
        }

        return save;
    }

    @CacheEvict(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public void deleteById(Long commentId) {
        Long postId = findById(commentId).getPost().getId();
        repository.deleteById(commentId);

        String allCommentIds =
                cacheKeyGenerator(ALL_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(allCommentIds)) {
            log.info("comment 삭제후 postId에 대한 모든 commentId 캐시 삭제 postId = {}", postId);
            redisService.removeFromSet(allCommentIds, postId);
        }

        String recentCommentIds =
                cacheKeyGenerator(RECENT_COMMENT_IDS, POST_ID, postId.toString());
        if (redisService.hasKey(recentCommentIds)) {
            log.info("comment 삭제후 postId에 대한 최근 commentId 캐시 삭제 postId = {}", postId);
            redisService.removeFromSet(recentCommentIds, postId);
        }
    }

}
