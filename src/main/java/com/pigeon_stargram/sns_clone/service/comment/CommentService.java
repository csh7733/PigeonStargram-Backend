package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
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
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final ReplyCrudService replyCrudService;

    public List<ResponseCommentDto> getCommentDtosByPostId(Long postId) {
        List<Long> commentIds = commentCrudService.findCommentIdByPostId(postId);
        return commentIds.stream()
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedComment)
                .collect(Collectors.toList());
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

        dto.setLoginUserName(loginUser.getName());
        notificationService.sendToSplitWorker(dto);

        notifyTaggedUsers(dto, loginUser);

        return save;
    }

    private void notifyTaggedUsers(CreateCommentDto dto, User loginUser) {
        NotifyCommentTaggedDto notifyCommentTaggedDto =
                buildNotifyCommentTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyCommentTaggedDto);
    }

    public void editComment(EditCommentDto dto) {
        commentCrudService.edit(dto.getCommentId(), dto.getContent());
    }

    public void deleteAllCommentsAndReplyByPostId(Long postId) {
        List<Long> commentIds = commentCrudService.findCommentIdByPostId(postId);
        commentIds.forEach(this::deleteComment);
    }

    public void deleteComment(Long commentId) {
        replyService.deleteAllReplyByCommentId(commentId);
        commentCrudService.deleteById(commentId);
    }

    public CommentLikeDto getCommentLike(Long commentId) {
        Integer count = commentLikeCrudService.countByCommentId(commentId);
        return buildCommentLikeDto(false, count);
    }

    public void likeComment(LikeCommentDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long commentId = dto.getCommentId();

        User loginUser = userService.findById(loginUserId);
        dto.setLoginUserName(loginUser.getName());

        Comment comment = commentCrudService.findById(commentId);
        dto.setWriterId(comment.getUser().getId());

        commentLikeCrudService.toggleLike(loginUserId, commentId);

        // 좋아요수가 증가할때 알림 보내기
        List<Long> commentLikeUserIds = commentLikeCrudService.getCommentLikeUserIds(commentId);
        if (commentLikeUserIds.contains(loginUserId)) {
            notificationService.sendToSplitWorker(dto);
        }
    }
}
