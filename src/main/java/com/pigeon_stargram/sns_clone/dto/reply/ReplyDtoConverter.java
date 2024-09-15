package com.pigeon_stargram.sns_clone.dto.reply;

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

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.*;

/**
 * 답글 관련 데이터 전송 객체(DTO)와 도메인 모델 간의 변환을 수행하는 유틸리티 클래스입니다.
 *
 * 이 클래스는 다양한 DTO와 도메인 모델 간의 변환 메서드를 제공하여,
 * 데이터 변환과 관련된 공통 로직을 관리합니다.
 */
public class ReplyDtoConverter {

    /**
     * RequestAddReplyDto를 CreateReplyDto로 변환합니다.
     *
     * @param dto 요청 데이터 전송 객체
     * @param loginUser 현재 로그인된 사용자
     * @param commentUserId 댓글 작성자 ID
     * @return 변환된 CreateReplyDto 객체
     */
    public static CreateReplyDto toCreateReplyDto(RequestAddReplyDto dto,
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

    /**
     * CreateReplyDto를 NotifyReplyTaggedDto로 변환합니다.
     *
     * @param dto 생성된 답글 DTO
     * @param loginUser 현재 로그인된 사용자
     * @return 변환된 NotifyReplyTaggedDto 객체
     */
    public static NotifyReplyTaggedDto toNotifyReplyTaggedDto(CreateReplyDto dto,
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

    /**
     * RequestEditReplyDto를 EditReplyDto로 변환합니다.
     *
     * @param dto 요청 데이터 전송 객체
     * @param replyId 수정할 답글 ID
     * @return 변환된 EditReplyDto 객체
     */
    public static EditReplyDto toEditReplyDto(RequestEditReplyDto dto,
                                              Long replyId) {
        return EditReplyDto.builder()
                .replyId(replyId)
                .content(dto.getContent())
                .build();
    }

    /**
     * RequestLikeReplyDto를 LikeReplyDto로 변환합니다.
     *
     * @param dto 요청 데이터 전송 객체
     * @param loginUser 현재 로그인된 사용자
     * @return 변환된 LikeReplyDto 객체
     */
    public static LikeReplyDto toLikeReplyDto(RequestLikeReplyDto dto,
                                              SessionUser loginUser) {
        return LikeReplyDto.builder()
                .loginUserId(loginUser.getId())
                .replyId(dto.getReplyId())
                .postUserId(dto.getPostUserId())
                .postId(dto.getPostId())
                .build();
    }

    /**
     * ReplyContentDto와 ReplyLikeDto를 사용하여 ResponseReplyDto를 생성합니다.
     *
     * @param contentDto 답글 내용 DTO
     * @param likeDto 답글 좋아요 DTO
     * @return 생성된 ResponseReplyDto 객체
     */
    public static ResponseReplyDto toResponseReplyDto(ReplyContentDto contentDto,
                                                      ReplyLikeDto likeDto) {
        ReplyDataDto dataDto = toReplyDataDto(contentDto, likeDto);
        return ResponseReplyDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(dataDto)
                .build();
    }

    /**
     * ReplyContentDto와 ReplyLikeDto를 사용하여 ReplyDataDto를 생성합니다.
     *
     * @param contentDto 답글 내용 DTO
     * @param likeDto 답글 좋아요 DTO
     * @return 생성된 ReplyDataDto 객체
     */
    public static ReplyDataDto toReplyDataDto(ReplyContentDto contentDto,
                                              ReplyLikeDto likeDto) {
        return ReplyDataDto.builder()
                .comment(contentDto.getComment())
                .likes(likeDto)
                .build();
    }

    /**
     * Reply 객체를 ReplyContentDto로 변환합니다.
     *
     * @param reply 답글 도메인 모델
     * @return 변환된 ReplyContentDto 객체
     */
    public static ReplyContentDto toReplyContentDto(Reply reply) {
        ReplyProfileDto profileDto =
                toReplyProfileDto(reply.getUser(), reply.getModifiedDate());
        return ReplyContentDto.builder()
                .id(reply.getId())
                .comment(reply.getContent())
                .profile(profileDto)
                .build();
    }

    /**
     * User와 답글 수정 날짜를 사용하여 ReplyProfileDto를 생성합니다.
     *
     * @param user 사용자 도메인 모델
     * @param modifiedDate 답글 수정 날짜
     * @return 생성된 ReplyProfileDto 객체
     */
    public static ReplyProfileDto toReplyProfileDto(User user,
                                                    LocalDateTime modifiedDate) {
        return ReplyProfileDto.builder()
                .id(user.getId())
                .avatar(user.getAvatar())
                .name(user.getName())
                .time(formatTime(modifiedDate))
                .build();
    }

    /**
     * 좋아요 여부와 값을 사용하여 ReplyLikeDto를 생성합니다.
     *
     * @param like 좋아요 여부
     * @param value 좋아요 수
     * @return 생성된 ReplyLikeDto 객체
     */
    public static ReplyLikeDto toReplyLikeDto(Boolean like,
                                              Integer value) {
        return ReplyLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }
}
