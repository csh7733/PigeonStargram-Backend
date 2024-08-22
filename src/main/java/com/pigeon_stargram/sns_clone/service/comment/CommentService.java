package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostCrudService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.pigeon_stargram.sns_clone.service.comment.CommentBuilder.*;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentCrudService commentCrudService;
    private final UserService userService;
    private final PostCrudService postCrudService;
    private final ReplyService replyService;
    private final NotificationService notificationService;
    private final CommentLikeCrudService commentLikeCrudService;

    public List<ResponseCommentDto> getCommentDtosByPostId(Long postId) {
        List<Comment> comments = commentCrudService.findByPostId(postId);
        return comments.stream()
                .map(Comment::getId)
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedComment)
                .toList();
    }

    public ResponseCommentDto getCombinedComment(Long commentId) {
        CommentContentDto contentDto = getCommentContent(commentId);

        CommentLikeDto likeDto = getCommentLike(commentId);

        List<ResponseReplyDto> replyDtos = replyService.getReplyDtosByCommentId(commentId);

        return buildResponseCommentDto(contentDto, likeDto, replyDtos);
    }

    public CommentContentDto getCommentContent(Long commentId) {
        Comment comment = commentCrudService.findById(commentId);
        return buildCommentContentDto(comment);
    }

    public Comment createComment(CreateCommentDto dto) {
        User loginUser = userService.findById(dto.getLoginUserId());
        Post post = postCrudService.findById(dto.getPostId());

        Comment comment = buildComment(dto, loginUser, post);
        Comment save = commentCrudService.save(comment);

        notificationService.send(dto);

        notifyTaggedUsers(dto, loginUser);

        return save;
    }

    private void notifyTaggedUsers(CreateCommentDto dto, User loginUser) {
        NotifyCommentTaggedDto notifyCommentTaggedDto =
                buildNotifyCommentTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyCommentTaggedDto);
    }

    public void editComment(EditCommentDto dto) {
        Comment comment = commentCrudService.findById(dto.getCommentId());
        comment.modify(dto.getContent());
    }

    public void deleteAllCommentsAndReplyByPostId(Long postId) {
        List<Comment> comments = commentCrudService.findByPostId(postId);
        comments.forEach(comment -> {
            replyService.deleteAllRepliesByCommentId(comment.getId());
            commentCrudService.deleteById(comment.getId());
        });
    }

    public CommentLikeDto getCommentLike(Long commentId) {
        Integer count = commentLikeCrudService.countByCommentId(commentId);
        return buildCommentLikeDto(false, count);
    }

    public void likeComment(LikeCommentDto dto) {
        User loginUser = userService.findById(dto.getLoginUserId());
        Comment comment = commentCrudService.findById(dto.getCommentId());
        dto.setWriterId(comment.getUser().getId());

        commentLikeCrudService.findByUserIdAndCommentId(dto.getLoginUserId(), comment.getId())
                .ifPresentOrElse(
                        existingLike -> {
                            commentLikeCrudService.delete(existingLike);
                        },
                        () -> {
                            CommentLike commentLike = buildCommentLike(loginUser, comment);
                            commentLikeCrudService.save(commentLike);
                            notificationService.send(dto);
                        }
                );
    }
}
