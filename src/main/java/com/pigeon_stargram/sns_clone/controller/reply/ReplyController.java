package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestDeleteReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/replies")
@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final PostsService postsService;
    private final TimelineService timelineService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final UserService userService;
    private final NotificationService notificationService;

    @PostMapping
    public List<ResponsePostsDto> addReply(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestAddReplyDto request) {
        Long postId = request.getPostId();

        Long commentId = request.getCommentId();
        Comment comment = commentService.getCommentEntity(commentId);
        String content = request.getReply().getContent();

        Long userId = loginUser.getId();
        String context = request.getContext();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        CreateReplyDto createReplyDto = CreateReplyDto.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        replyService.createReply(createReplyDto);

        NotifyReplyTaggedUsersDto notifyTaggedUsers = NotifyReplyTaggedUsersDto.builder()
                .user(user)
                .content(content)
                .notificationRecipientIds(request.getReply().getTaggedUserIds())
                .postUserId(postUserId)
                .postId(postId)
                .build();

        notificationService.notifyTaggedUsers(notifyTaggedUsers);

        return getPostsBasedOnContext(context, userId, postUser.getId());
    }

    @PatchMapping("/{replyId}")
    public List<ResponsePostsDto> editReply(@LoginUser SessionUser loginUser,
                                            @PathVariable Long replyId,
                                            @RequestBody RequestEditReplyDto request) {
        Long userId = loginUser.getId();
        String context = request.getContext();

        Long postUserId = request.getPostUserId();

        String content = request.getContent();
        replyService.editReply(replyId,content);

        return getPostsBasedOnContext(context, userId, postUserId);
    }
    @DeleteMapping("/{replyId}")
    public List<ResponsePostsDto> deleteReply(@LoginUser SessionUser loginUser,
                                              @PathVariable Long replyId,
                                              @RequestBody RequestDeleteReplyDto request) {
        Long userId = loginUser.getId();
        String context = request.getContext();

        Long postUserId = request.getPostUserId();

        replyService.deleteReply(replyId);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likeReply(@LoginUser SessionUser loginUser,
                                            @RequestBody RequestLikeReplyDto request) {
        Long postId = request.getPostId();

        Long userId = loginUser.getId();
        String context = request.getContext();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();

        Long replyId = request.getReplyId();

        LikeReplyDto likeReplyDto = LikeReplyDto.builder()
                .user(user)
                .replyId(replyId)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        replyService.likeReply(likeReplyDto);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    private List<ResponsePostsDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if ("timeline".equals(context)) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postsService.getPostsByUser(postUserId);
        }
    }
}
