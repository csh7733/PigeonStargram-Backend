package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentDataDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentProfileDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

public class CommentBuilder {

    private CommentBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static NotifyCommentTaggedDto buildNotifyCommentTaggedDto(CreateCommentDto dto,
                                                                     User loginUser) {
        return NotifyCommentTaggedDto.builder()
                .userId(loginUser.getId())
                .userName(loginUser.getName())
                .content(dto.getContent())
                .notificationRecipientIds(dto.getTaggedUserIds())
                .postUserId(dto.getPostUserId())
                .postId(dto.getPostId())
                .build();
    }

    public static Comment buildComment(CreateCommentDto dto,
                                        User loginUser,
                                        Post post) {
        return Comment.builder()
                .user(loginUser)
                .post(post)
                .content(dto.getContent())
                .build();
    }

    public static CommentContentDto buildCommentContentDto(Comment comment) {
        CommentProfileDto profileDto =
                buildCommentProfileDto(comment.getUser(), comment.getModifiedDate());
        return CommentContentDto.builder()
                .id(comment.getId())
                .comment(comment.getContent())
                .profile(profileDto)
                .build();
    }

    public static CommentProfileDto buildCommentProfileDto(User user,
                                                           LocalDateTime modifiedDate) {
        return CommentProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .time(LocalDateTimeUtil.formatTime(modifiedDate))
                .build();
    }

    public static CommentLikeDto buildCommentLikeDto(Boolean like,
                                                     Integer value) {
        return CommentLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }

    public static ResponseCommentDto buildResponseCommentDto(CommentContentDto contentDto,
                                                             CommentLikeDto likeDto,
                                                             List<ResponseReplyDto> replyDtos) {
        return ResponseCommentDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(buildCommentDataDto(contentDto, likeDto, replyDtos))
                .build();
    }

    public static CommentDataDto buildCommentDataDto(CommentContentDto contentDto,
                                                     CommentLikeDto likeDto,
                                                     List<ResponseReplyDto> replyDtos) {
        return CommentDataDto.builder()
                .comment(contentDto.getComment())
                .likes(likeDto)
                .replies(replyDtos)
                .build();
    }

    public static CreateCommentDto buildCreateCommentDto(RequestAddCommentDto dto,
                                                         SessionUser loginUser) {
        return CreateCommentDto.builder()
                .loginUserId(loginUser.getId())
                .postId(dto.getPostId())
                .postUserId(dto.getPostUserId())
                .context(dto.getContext())
                .content(dto.getComment().getContent())
                .taggedUserIds(dto.getComment().getTaggedUserIds())
                .build();
    }

    public static EditCommentDto buildEditCommentDto(Long commentId,
                                                     String content) {
        return EditCommentDto.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }

    public static LikeCommentDto buildLikeCommentDto(RequestLikeCommentDto dto,
                                                     SessionUser loginUser) {
        return LikeCommentDto.builder()
                .loginUserId(loginUser.getId())
                .postUserId(dto.getPostUserId())
                .commentId(dto.getCommentId())
                .postId(dto.getPostId())
                .build();
    }

    public static CommentLike buildCommentLike(User loginUser,
                                               Comment comment) {
        return CommentLike.builder()
                .user(loginUser)
                .comment(comment)
                .build();
    }
}
