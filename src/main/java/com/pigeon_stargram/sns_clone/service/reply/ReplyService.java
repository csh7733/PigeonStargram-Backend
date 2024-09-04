package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedDto;
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

import static com.pigeon_stargram.sns_clone.service.reply.ReplyBuilder.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ReplyService {

    private final ReplyCrudService replyCrudService;
    private final ReplyLikeCrudService replyLikeCrudService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CommentCrudService commentCrudService;

    public List<ResponseReplyDto> getReplyDtosByCommentId(Long commentId) {
        return replyCrudService.findReplyIdByCommentId(commentId).stream()
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedReply)
                .collect(Collectors.toList());
    }

    public ResponseReplyDto getCombinedReply(Long replyId) {
        ReplyContentDto replyContentDto = getReplyContent(replyId);
        ReplyLikeDto replyLikeDto = getReplyLike(replyId);
        return buildResponseReplyDto(replyContentDto, replyLikeDto);
    }

    public ReplyContentDto getReplyContent(Long replyId) {
        Reply reply = replyCrudService.findById(replyId);
        return buildReplyContentDto(reply);
    }

    public ReplyLikeDto getReplyLike(Long replyId) {
        Integer count = replyLikeCrudService.countByReplyId(replyId);
        return buildReplyLikeDto(false, count);
    }

    public Reply createReply(CreateReplyDto dto) {
        User loginUser = userService.findById(dto.getLoginUserId());
        Comment comment = commentCrudService.findById(dto.getCommentId());

        Reply reply = buildReply(dto, loginUser, comment);
        Reply save = replyCrudService.save(reply);

        dto.setLoginUserName(loginUser.getName());
        notificationService.send(dto);

        notifyTaggedUsers(dto, loginUser);

        return save;
    }

    private void notifyTaggedUsers(CreateReplyDto dto, User loginUser) {
        NotifyReplyTaggedDto notifyTaggedUsers =
                buildNotifyReplyTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyTaggedUsers);
    }

    public void editReply(EditReplyDto dto) {
        replyCrudService.edit(dto.getReplyId(), dto.getContent());
    }

    public void likeReply(LikeReplyDto dto) {
        // todo 좋아요 추가시 알림
        User loginUser = userService.findById(dto.getLoginUserId());
        dto.setLoginUserName(loginUser.getName());
        replyLikeCrudService.toggleLike(dto.getLoginUserId(), dto.getReplyId());
    }

    public void deleteAllReplyByCommentId(Long commentId) {
        List<Long> replyIds = replyCrudService.findReplyIdByCommentId(commentId);
        replyIds.forEach(replyCrudService::deleteById);
    }
}

