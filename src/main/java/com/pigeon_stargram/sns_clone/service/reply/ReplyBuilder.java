package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyDataDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyLikeDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyProfileDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;
import jakarta.mail.Session;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;

public class ReplyBuilder {

    private ReplyBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static CreateReplyDto buildCreateReplyDto(RequestAddReplyDto dto,
                                                     SessionUser loginUser,
                                                     Long commentUserId) {
        return CreateReplyDto.builder()
                .loginUserId(loginUser.getId())
                .commentUserId(commentUserId)
                .commentId(dto.getCommentId())
                .content(dto.getReply().getContent())
                .postUserId(dto.getPostUserId())
                .postId(dto.getPostId())
                .taggedUserIds(dto.getReply().getTaggedUserIds())
                .build();
    }

    public static NotifyReplyTaggedDto buildNotifyReplyTaggedDto(CreateReplyDto dto,
                                                                 User loginUser) {
        return NotifyReplyTaggedDto.builder()
                .userId(loginUser.getId())
                .userName(loginUser.getName())
                .content(dto.getContent())
                .postUserId(dto.getPostUserId())
                .postId(dto.getPostId())
                .notificationRecipientIds(dto.getTaggedUserIds())
                .build();
    }

    public static Reply buildReply(CreateReplyDto dto,
                                   User user,
                                   Comment comment) {
        return Reply.builder()
                .user(user)
                .comment(comment)
                .content(dto.getContent())
                .build();
    }

    public static EditReplyDto buildEditReplyDto(RequestEditReplyDto dto,
                                                 Long replyId) {
        return EditReplyDto.builder()
                .replyId(replyId)
                .content(dto.getContent())
                .build();
    }

    public static LikeReplyDto buildLikeReplyDto(RequestLikeReplyDto dto,
                                                 SessionUser loginUser) {
        return LikeReplyDto.builder()
                .loginUserId(loginUser.getId())
                .replyId(dto.getReplyId())
                .postUserId(dto.getPostUserId())
                .postId(dto.getPostId())
                .build();
    }

    public static ReplyLike buildReplyLike(User loginUser,
                                           Reply reply) {
        return ReplyLike.builder()
                .user(loginUser)
                .reply(reply)
                .build();
    }

    public static ResponseReplyDto buildResponseReplyDto(ReplyContentDto contentDto,
                                                         ReplyLikeDto likeDto) {
        ReplyDataDto dataDto = buildReplyDataDto(contentDto, likeDto);
        return ResponseReplyDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(dataDto)
                .build();
    }

    public static ReplyDataDto buildReplyDataDto(ReplyContentDto contentDto,
                                                 ReplyLikeDto likeDto) {
        return ReplyDataDto.builder()
                .comment(contentDto.getComment())
                .likes(likeDto)
                .build();
    }

    public static ReplyContentDto buildReplyContentDto(Reply reply) {
        ReplyProfileDto profileDto =
                buildReplyProfileDto(reply.getUser(), reply.getModifiedDate());
        return ReplyContentDto.builder()
                .id(reply.getId())
                .comment(reply.getContent())
                .profile(profileDto)
                .build();
    }

    public static ReplyProfileDto buildReplyProfileDto(User user,
                                                       LocalDateTime modifiedDate) {
        return ReplyProfileDto.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .time(formatTime(modifiedDate))
                .build();
    }

    public static ReplyLikeDto buildReplyLikeDto(Boolean like,
                                                 Integer value) {
        return ReplyLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }

}
