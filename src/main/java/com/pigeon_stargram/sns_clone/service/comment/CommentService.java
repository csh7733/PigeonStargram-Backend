package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final ReplyService replyService;
    private final NotificationService notificationService;

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public List<ResponseCommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(Comment::getId)
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedComment)
                .toList();
    }

    public ResponseCommentDto getCombinedComment(Long commentId) {
        CommentContentDto commentContentDto = getCommentContent(commentId);
        CommentLikeDto commentLikeDto = getCommentLike(commentId);
        List<ResponseReplyDto> replyDtos = replyService.getRepliesByCommentId(commentId);
        return new ResponseCommentDto(commentContentDto, commentLikeDto, replyDtos);

    }

    public CommentContentDto getCommentContent(Long commentId) {
        return new CommentContentDto(getCommentEntity(commentId));
    }

    public CommentLikeDto getCommentLike(Long commentId) {
        Integer count = commentLikeRepository.countByCommentId(commentId);
        return new CommentLikeDto(false, count);
    }


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

    public List<ResponseCommentDto> getCommentListByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(comment -> {
                    List<ResponseReplyDto> replies = replyService.getReplyListByComment(comment.getId());
                    return new ResponseCommentDto(comment, replies);
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
