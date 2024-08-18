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

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        commentService.createComment(new CreateCommentDto(user, post, content));

        NotifyCommentTaggedUsersDto notifyTaggedUsers = NotifyCommentTaggedUsersDto.builder()
                .user(user)
                .content(content)
                .notificationRecipientIds(request.getComment().getTaggedUserIds())
                .postUserId(postUserId)
                .postId(postId)
                .build();

        notificationService.notifyTaggedUsers(notifyTaggedUsers);

        return postsService.getPostsByUser(postUser.getId());
    }

    @PatchMapping("/{commentId}")
    public List<ResponsePostsDto> editComment(@LoginUser SessionUser loginUser,
                                              @PathVariable Long commentId,
                                              @RequestBody RequestEditCommentDto request) {
        String content = request.getContent();

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        commentService.editComment(commentId,content);

        return postsService.getPostsByUser(postUser.getId());
    }

    @DeleteMapping("/{commentId}")
    public List<ResponsePostsDto> deleteComment(@LoginUser SessionUser loginUser,
                                                @PathVariable Long commentId,
                                                @RequestBody RequestDeleteCommentDto request) {
        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        commentService.deleteComment(commentId);

        return postsService.getPostsByUser(postUser.getId());
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likeComment(@LoginUser SessionUser loginUser,
                                              @RequestBody RequestLikeCommentDto request) {
        Long postId = request.getPostId();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        Long commentId = request.getCommentId();

        LikeCommentDto likeCommentDto = LikeCommentDto.builder()
                .user(user)
                .commentId(commentId)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        commentService.likeComment(likeCommentDto);

        return postsService.getPostsByUser(postUser.getId());
    }
}
