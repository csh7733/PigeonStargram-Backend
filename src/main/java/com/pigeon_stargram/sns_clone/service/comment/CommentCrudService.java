package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.COMMENT_NOT_FOUND_ID;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentCrudService {

    private final ReplyService replyService;

    private final CommentRepository repository;

    public Comment findById(Long commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND_ID));
    }

    public List<Comment> findByPostId(Long postId) {
        return repository.findByPostId(postId);
    }

    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    public void deleteById(Long commentId) {
        replyService.deleteAllRepliesByCommentId(commentId);
        repository.deleteById(commentId);
    }

}
