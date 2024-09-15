package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentFactory;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostCrudService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.dto.comment.CommentDtoConverter.*;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceV2 implements CommentService{

    private final CommentCrudService commentCrudService;
    private final UserService userService;
    private final PostCrudService postCrudService;
    private final ReplyService replyService;
    private final NotificationService notificationService;
    private final CommentLikeCrudServiceV2 commentLikeCrudService;
    private final CommentRepository commentRepository;

    @Override
    public Comment findById(Long commentId) {
        return commentCrudService.findById(commentId);
    }

    @Override
    public ResponseGetCommentDto getPartialComment(RequestGetCommentDto dto) {
        Long postId = dto.getPostId();
        Long lastCommentId = dto.getLastCommentId();

        // 포스트 ID와 마지막 댓글 ID를 기준으로 댓글 목록을 조회합니다.
        List<ResponseCommentDto> comments =
                getCommentResponseByPostIdAndLastCommentId(postId, lastCommentId);
        // 추가 댓글이 있는지 확인합니다.
        Boolean isMoreComments = commentCrudService.getIsMoreComments(postId, lastCommentId);

        return toResponseGetCommentDto(comments, isMoreComments);
    }

    @Override
    public List<ResponseCommentDto> getCommentResponseByPostIdAndLastCommentId(Long postId,
                                                                               Long commentId) {
        List<Long> commentIds = commentCrudService.findCommentIdByPostIdAndCommentId(postId, commentId);
        return commentIds.stream()
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedComment)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCommentDto getCombinedComment(Long commentId) {
        // 댓글 ID를 기준으로 댓글 내용을 조회합니다.
        CommentContentDto contentDto = getCommentContent(commentId);
        // 댓글 ID를 기준으로 댓글 좋아요 정보를 조회합니다.
        CommentLikeDto likeDto = getCommentLike(commentId);
        // 댓글 ID를 기준으로 댓글에 대한 답글 목록을 조회합니다.
        List<ResponseReplyDto> replyDtos = replyService.getReplyDtosByCommentId(commentId);

        return toResponseCommentDto(contentDto, likeDto, replyDtos);
    }

    @Override
    public CommentContentDto getCommentContent(Long commentId) {
        Comment comment = commentCrudService.findById(commentId);
        return toCommentContentDto(comment);
    }

    @Override
    public ResponseCommentDto createComment(CreateCommentDto dto) {
        User loginUser = userService.getUserById(dto.getLoginUserId());
        Post post = postCrudService.findById(dto.getPostId());

        // 댓글을 생성하고 저장합니다.
        Comment comment = CommentFactory.createComment(dto, loginUser, post);
        commentCrudService.save(comment);

        // 댓글에 태그된 사용자에게 알림을 보냅니다.
        notifyTaggedUsers(dto, loginUser);

        return getCombinedComment(comment.getId());
    }

    @Override
    public void editComment(EditCommentDto dto) {
        commentCrudService.edit(dto.getCommentId(), dto.getContent());
    }

    @Override
    public void deleteAllCommentsAndReplyByPostId(Long postId) {
        List<Long> commentIds = commentCrudService.findCommentIdByPostId(postId);
        commentIds.forEach(this::deleteComment);
    }

    @Override
    public void deleteComment(Long commentId) {
        // 댓글 ID를 기준으로 답글을 모두 삭제합니다.
        replyService.deleteAllReplyByCommentId(commentId);
        // 댓글을 삭제합니다.
        commentCrudService.deleteById(commentId);
    }

    @Override
    public CommentLikeDto getCommentLike(Long commentId) {
        Integer count = commentLikeCrudService.countByCommentId(commentId);
        return toCommentLikeDto(false, count);
    }

    @Override
    public Boolean likeComment(LikeCommentDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long commentId = dto.getCommentId();

        // 로그인 사용자가 댓글에 대해 좋아요를 토글합니다 (추가 또는 제거).
        commentLikeCrudService.toggleLike(loginUserId, commentId);

        // 알림을 위한 필드 세팅
        User loginUser = userService.getUserById(loginUserId);
        dto.setLoginUserName(loginUser.getName());

        Comment comment = commentCrudService.findById(commentId);
        dto.setWriterId(comment.getUser().getId());

        // 좋아요수가 증가할때 알림 보내고 true 반환
        List<Long> commentLikeUserIds = commentLikeCrudService.getCommentLikeUserIds(commentId);
        if (commentLikeUserIds.contains(loginUserId)) {
            notificationService.sendToSplitWorker(dto);
            return true;
        }
        return false;
    }

    private void notifyTaggedUsers(CreateCommentDto dto,
                                   User loginUser) {
        dto.setLoginUserName(loginUser.getName());
        // 댓글에 태그된 사용자에게 알림을 보냅니다.
        notificationService.sendToSplitWorker(dto);

        NotifyCommentTaggedDto notifyCommentTaggedDto =
                toNotifyCommentTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyCommentTaggedDto);
    }
}
