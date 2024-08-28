package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.COMMENT;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.COMMENT_NOT_FOUND_ID;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentCrudService {

    private final CommentRepository repository;
    private final ReplyCrudService replyCrudService;

    @Cacheable(value = COMMENT,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).COMMENT_ID + '_' + #commentId")
    public Comment findById(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
    }

    public List<Comment> findCommentIdByPostId(Long postId) {
        return repository.findByPostId(postId);
    }

    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    public void deleteById(Long commentId) {
        replyCrudService.deleteAllByCommentId(commentId);
        repository.deleteById(commentId);
    }

}
