package com.pigeon_stargram.sns_clone.dto.comment;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.*;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글과 관련된 DTO 간의 변환을 담당하는 클래스입니다.
 * <p>
 * 이 클래스는 다양한 요청 및 응답 데이터에 대해 DTO를 변환하는 메서드를 제공합니다.
 * </p>
 */
public class CommentDtoConverter {

    /**
     * 게시물 ID와 마지막 댓글 ID를 사용하여 댓글 조회 요청 DTO를 생성합니다.
     * @param postId 게시물 ID
     * @param lastCommentId 마지막 댓글 ID
     * @return 댓글 조회 요청 DTO
     */
    public static RequestGetCommentDto toRequestGetCommentDto(Long postId,
                                                              Long lastCommentId) {
        return RequestGetCommentDto.builder()
                .postId(postId)
                .lastCommentId(lastCommentId)
                .build();
    }

    /**
     * 댓글 추가 요청 DTO와 로그인 사용자를 사용하여 댓글 생성 DTO를 생성합니다.
     * @param dto 댓글 추가 요청 DTO
     * @param loginUser 로그인 사용자 정보
     * @return 댓글 생성 DTO
     */
    public static CreateCommentDto toCreateCommentDto(RequestAddCommentDto dto,
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

    /**
     * 댓글 ID와 콘텐츠를 사용하여 댓글 수정 DTO를 생성합니다.
     * @param commentId 댓글 ID
     * @param content 댓글 콘텐츠
     * @return 댓글 수정 DTO
     */
    public static EditCommentDto toEditCommentDto(Long commentId,
                                                  String content) {
        return EditCommentDto.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }

    /**
     * 댓글 좋아요 요청 DTO와 로그인 사용자를 사용하여 댓글 좋아요 DTO를 생성합니다.
     * @param dto 댓글 좋아요 요청 DTO
     * @param loginUser 로그인 사용자 정보
     * @return 댓글 좋아요 DTO
     */
    public static LikeCommentDto toLikeCommentDto(RequestLikeCommentDto dto,
                                                  SessionUser loginUser) {
        return LikeCommentDto.builder()
                .loginUserId(loginUser.getId())
                .postUserId(dto.getPostUserId())
                .commentId(dto.getCommentId())
                .postId(dto.getPostId())
                .build();
    }

    /**
     * 댓글 목록과 추가 댓글 여부를 사용하여 댓글 조회 응답 DTO를 생성합니다.
     * @param comments 댓글 목록
     * @param isMoreComments 추가 댓글 여부
     * @return 댓글 조회 응답 DTO
     */
    public static ResponseGetCommentDto toResponseGetCommentDto(List<ResponseCommentDto> comments,
                                                                Boolean isMoreComments) {
        return ResponseGetCommentDto.builder()
                .comments(comments)
                .isMoreComments(isMoreComments)
                .build();
    }

    /**
     * 댓글 생성 DTO와 로그인 사용자를 사용하여 댓글 태그 알림 DTO를 생성합니다.
     * @param dto 댓글 생성 DTO
     * @param loginUser 로그인 사용자 정보
     * @return 댓글 태그 알림 DTO
     */
    public static NotifyCommentTaggedDto toNotifyCommentTaggedDto(CreateCommentDto dto,
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

    /**
     * 댓글 내용 DTO, 좋아요 DTO, 답글 DTO 목록을 사용하여 응답 댓글 DTO를 생성합니다.
     * @param contentDto 댓글 내용 DTO
     * @param likeDto 댓글 좋아요 DTO
     * @param replyDtos 답글 DTO 목록
     * @return 응답 댓글 DTO
     */
    public static ResponseCommentDto toResponseCommentDto(CommentContentDto contentDto,
                                                          CommentLikeDto likeDto,
                                                          List<ResponseReplyDto> replyDtos) {
        return ResponseCommentDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(toCommentDataDto(contentDto, likeDto, replyDtos))
                .build();
    }

    /**
     * 댓글 객체를 사용하여 댓글 내용 DTO를 생성합니다.
     * @param comment 댓글 객체
     * @return 댓글 내용 DTO
     */
    public static CommentContentDto toCommentContentDto(Comment comment) {
        CommentProfileDto profileDto =
                toCommentProfileDto(comment.getUser(), comment.getModifiedDate());
        return CommentContentDto.builder()
                .id(comment.getId())
                .comment(comment.getContent())
                .profile(profileDto)
                .build();
    }

    /**
     * 사용자와 댓글 수정 시간을 사용하여 댓글 프로필 DTO를 생성합니다.
     * @param user 사용자 객체
     * @param modifiedDate 댓글 수정 시간
     * @return 댓글 프로필 DTO
     */
    public static CommentProfileDto toCommentProfileDto(User user,
                                                        LocalDateTime modifiedDate) {
        return CommentProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .time(LocalDateTimeUtil.formatTime(modifiedDate))
                .build();
    }

    /**
     * 댓글 좋아요 상태와 값을 사용하여 댓글 좋아요 DTO를 생성합니다.
     * @param like 좋아요 상태
     * @param value 좋아요 값
     * @return 댓글 좋아요 DTO
     */
    public static CommentLikeDto toCommentLikeDto(Boolean like,
                                                  Integer value) {
        return CommentLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }

    /**
     * 댓글 내용 DTO, 좋아요 DTO, 답글 DTO 목록을 사용하여 댓글 데이터 DTO를 생성합니다.
     * @param contentDto 댓글 내용 DTO
     * @param likeDto 댓글 좋아요 DTO
     * @param replyDtos 답글 DTO 목록
     * @return 댓글 데이터 DTO
     */
    public static CommentDataDto toCommentDataDto(CommentContentDto contentDto,
                                                  CommentLikeDto likeDto,
                                                  List<ResponseReplyDto> replyDtos) {
        return CommentDataDto.builder()
                .comment(contentDto.getComment())
                .likes(likeDto)
                .replies(replyDtos)
                .build();
    }
}
