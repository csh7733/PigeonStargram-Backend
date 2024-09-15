package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyFactory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.ReplyDtoConverter;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyLikeDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.dto.reply.ReplyDtoConverter.*;

/**
 * ReplyServiceV2는 답글과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>
 * 답글 생성, 수정, 삭제, 좋아요 등의 주요 기능을 제공하며, 알림 기능과 관련된 로직도 함께 처리합니다.
 * </p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReplyServiceV2 implements ReplyService{

    private final ReplyCrudServiceV2 replyCrudService;
    private final ReplyLikeCrudServiceV2 replyLikeCrudService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CommentCrudService commentCrudService;

    @Override
    public Reply findById(Long replyId) {
        return replyCrudService.findById(replyId);
    }

    @Override
    public List<ResponseReplyDto> getReplyDtosByCommentId(Long commentId) {
        return replyCrudService.findReplyIdByCommentId(commentId).stream()
                .sorted(Comparator.reverseOrder())  // 최신 순으로 정렬
                .map(this::getCombinedReply)         // 답글 데이터를 조합하여 DTO로 변환
                .collect(Collectors.toList());
    }

    @Override
    public ResponseReplyDto getCombinedReply(Long replyId) {
        // 답글 내용
        ReplyContentDto replyContentDto = getReplyContent(replyId);
        // 좋아요 정보
        ReplyLikeDto replyLikeDto = getReplyLike(replyId);
        return toResponseReplyDto(replyContentDto, replyLikeDto);
    }

    @Override
    public ReplyContentDto getReplyContent(Long replyId) {
        Reply reply = replyCrudService.findById(replyId);
        return toReplyContentDto(reply);
    }

    @Override
    public ReplyLikeDto getReplyLike(Long replyId) {
        Integer count = replyLikeCrudService.countByReplyId(replyId);
        return toReplyLikeDto(false, count);
    }

    @Override
    public ResponseReplyDto createReply(CreateReplyDto dto) {
        User loginUser = userService.getUserById(dto.getLoginUserId());
        Comment comment = commentCrudService.findById(dto.getCommentId());
        // Reply 객체 생성 후 저장
        Reply reply = ReplyFactory.createReply(dto, loginUser, comment);
        replyCrudService.save(reply);

        // 댓글작성자에게 알림
        dto.setLoginUserName(loginUser.getName());
        notificationService.sendToSplitWorker(dto);
        // 태그된 사용자들에게 알림 전송
        notifyTaggedUsers(dto, loginUser);

        return getCombinedReply(reply.getId());
    }

    @Override
    public void editReply(EditReplyDto dto) {
        replyCrudService.edit(dto.getReplyId(), dto.getContent());
    }

    @Override
    public Boolean likeReply(LikeReplyDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long replyId = dto.getReplyId();

        // 좋아요 상태 토글
        replyLikeCrudService.toggleLike(loginUserId, replyId);

        // 알림을 위한 필드 설정
        User loginUser = userService.getUserById(loginUserId);
        dto.setLoginUserName(loginUser.getName());

        Reply reply = replyCrudService.findById(replyId);
        dto.setWriterId(reply.getUser().getId());

        // 좋아요가 추가된 경우 알림 전송후 true 반환
        List<Long> replyLikeUserIds = replyLikeCrudService.getReplyLikeUserIds(replyId);
        if (replyLikeUserIds.contains(loginUserId)) {
            notificationService.sendToSplitWorker(dto);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAllReplyByCommentId(Long commentId) {
        // 해당 댓글에 속한 답글 ID 리스트 조회후 전부 삭제
        List<Long> replyIds = replyCrudService.findReplyIdByCommentId(commentId);
        replyIds.forEach(replyCrudService::deleteById);                            
    }

    /**
     * 태그된 사용자들에게 알림을 전송합니다.
     *
     * @param dto       답글 생성 DTO
     * @param loginUser 답글 작성자
     */
    private void notifyTaggedUsers(CreateReplyDto dto, User loginUser) {
        NotifyReplyTaggedDto notifyTaggedUsers =
                toNotifyReplyTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyTaggedUsers);
    }

}

