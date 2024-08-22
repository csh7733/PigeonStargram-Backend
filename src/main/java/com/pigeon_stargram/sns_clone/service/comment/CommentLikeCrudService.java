package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentLikeCrudService {

    private final CommentLikeRepository repository;

    public Optional<CommentLike> findByUserIdAndCommentId(Long userId,
                                                          Long commentId) {
        return repository.findByUserIdAndCommentId(userId, commentId);
    }

    public CommentLike save(CommentLike commentLike) {
        return repository.save(commentLike);
    }

    public void delete(CommentLike commentLike) {
        repository.delete(commentLike);
    }

    public Integer countByCommentId(Long commentId) {
        return repository.countByCommentId(commentId);
    }
}
