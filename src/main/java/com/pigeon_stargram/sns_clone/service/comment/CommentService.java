package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.dto.comment.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyDto;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyService replyService;
    private final NotificationService notificationService;

    public Comment createComment(CreateCommentDto dto) {
        Comment comment = Comment.builder()
                .user(dto.getUser())
                .post(dto.getPost())
                .content(dto.getContent())
                .build();
        commentRepository.save(comment);
        notificationService.save(dto);
        return comment;
    }

//    public CommentDto getComment(Long commentId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
//        return new CommentDto(comment);
//    }

    public List<CommentDto> getCommentListByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> {
                    List<ReplyDto> replies = replyService.getReplyListByComment(comment.getId());
                    return new CommentDto(comment, replies);
                })
                .collect(Collectors.toList());
    }

    public void deleteAllCommentsAndReplyByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        comments.forEach(comment -> {
            replyService.deleteByCommentId(comment.getId());
            commentRepository.delete(comment);
        });
    }

    public void editComment(Long commentId, String newContent) {
        Comment comment = getCommentEntity(commentId);
        comment.modify(newContent);
    }

    public void likeComment(LikeCommentDto dto) {
        Comment comment = getCommentEntity(dto.getCommentId());
        dto.setWriterId(comment.getUser().getId());

        commentLikeRepository.findByUserAndComment(dto.getUser(), comment)
                .ifPresentOrElse(
                        existingLike -> {
                            commentLikeRepository.delete(existingLike);
                            comment.decrementLikes();
                        },
                        () -> {
                            CommentLike commentLike = CommentLike.builder()
                                    .user(dto.getUser())
                                    .comment(comment)
                                    .build();
                            commentLikeRepository.save(commentLike);
                            comment.incrementLikes();

                            notificationService.save(dto);
                        }
                );
    }

    public void deleteComment(Long commentId) {
        replyService.deleteByCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    public Comment getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }
}
