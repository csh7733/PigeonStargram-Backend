package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestDeleteCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestEditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostsService postsService;
    private final TimelineService timelineService;
    private final CommentService commentService;
    private final UserService userService;
    private final NotificationService notificationService;

    @PostMapping
    public List<ResponsePostsDto> addComment(@LoginUser SessionUser loginUser,
                                             @RequestBody RequestAddCommentDto request) {
        Long postId = request.getPostId();
        Posts post = postsService.getPostEntity(postId);
        String content = request.getComment().getContent();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);
        String context = request.getContext();

        Long postUserId = request.getPostUserId();

        commentService.createComment(new CreateCommentDto(user, post, content));

        NotifyCommentTaggedUsersDto notifyTaggedUsers = NotifyCommentTaggedUsersDto.builder()
                .user(user)
                .content(content)
                .notificationRecipientIds(request.getComment().getTaggedUserIds())
                .postUserId(postUserId)
                .postId(postId)
                .build();

        notificationService.notifyTaggedUsers(notifyTaggedUsers);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @PatchMapping("/{commentId}")
    public List<ResponsePostsDto> editComment(@LoginUser SessionUser loginUser,
                                              @PathVariable Long commentId,
                                              @RequestBody RequestEditCommentDto request) {
        Long userId = loginUser.getId();
        String context = request.getContext();

        String content = request.getContent();

        Long postUserId = request.getPostUserId();

        commentService.editComment(commentId,content);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @DeleteMapping("/{commentId}")
    public List<ResponsePostsDto> deleteComment(@LoginUser SessionUser loginUser,
                                                @PathVariable Long commentId,
                                                @RequestBody RequestDeleteCommentDto request) {
        Long userId = loginUser.getId();
        String context = request.getContext();

        Long postUserId = request.getPostUserId();

        commentService.deleteComment(commentId);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likeComment(@LoginUser SessionUser loginUser,
                                              @RequestBody RequestLikeCommentDto request) {
        Long postId = request.getPostId();
        String context = request.getContext();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();

        Long commentId = request.getCommentId();

        LikeCommentDto likeCommentDto = LikeCommentDto.builder()
                .user(user)
                .commentId(commentId)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        commentService.likeComment(likeCommentDto);

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
