package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final BasicUserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public List<ResponsePostsDto> getPosts(@RequestParam Long userId) {
        User user = userService.findById(userId);

        return postsService.getPostsByUser(user);
    }

    @PostMapping
    public List<ResponsePostsDto> createPosts(@LoginUser SessionUser loginUser,
                                        @RequestBody RequestCreatePostDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        String content = request.getContent();

        Posts post = postsService.createPost(new CreatePostDto(user, content));

        NotifyPostTaggedUsersDto notifyTaggedUsers = NotifyPostTaggedUsersDto.builder()
                .user(user)
                .content(content)
                .notificationRecipientIds(request.getTaggedUserIds())
                .build();

        notificationService.notifyTaggedUsers(notifyTaggedUsers);

        return postsService.getPostsByUser(user);
    }

    @PatchMapping("/{postId}")
    public List<ResponsePostsDto> editPost(@LoginUser SessionUser loginUser,
                                           @PathVariable Long postId,
                                           @RequestBody RequestEditPostDto request) {
        Long loginUserId = loginUser.getId();
        User user = userService.findById(loginUserId);

        String content = request.getContent();
        postsService.editPost(postId,content);

        return postsService.getPostsByUser(user);
    }

    @DeleteMapping("/{postId}")
    public List<ResponsePostsDto> deletePost(@LoginUser SessionUser loginUser,
                                             @PathVariable Long postId) {
        Long loginUserId = loginUser.getId();
        User user = userService.findById(loginUserId);

        postsService.deletePost(postId);

        return postsService.getPostsByUser(user);
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likePost(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestLikePostDto request) {
        Long postId = request.getPostId();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        LikePostDto likePostDto = LikePostDto.builder()
                .user(user)
                .postId(postId)
                .writerId(postUserId)
                .build();

        postsService.likePost(likePostDto);

        return postsService.getPostsByUser(postUser);
    }

}
