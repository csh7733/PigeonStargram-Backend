package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final TimelineService timelineService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final FileService fileService;


    @GetMapping
    public List<ResponsePostsDto> getPosts(@RequestParam Long userId) {
        return postsService.getPostsByUser(userId);
    }

    @PostMapping
    public List<ResponsePostsDto> createPosts(@LoginUser SessionUser loginUser,
                                              @ModelAttribute RequestCreatePostDto request,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> imagesFile) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        String content = request.getContent();

        Boolean hasImage = request.getHasImage();

        List<String> imageUrls = fileService.saveFiles(imagesFile);

        CreatePostDto createPostDto = CreatePostDto.builder()
                .user(user)
                .content(content)
                .imageUrls(imageUrls)
                .hasImage(hasImage)
                .build();

        Posts post = postsService.createPost(createPostDto);

        NotifyPostTaggedUsersDto notifyTaggedUsers = NotifyPostTaggedUsersDto.builder()
                .user(user)
                .content(content)
                .notificationRecipientIds(request.getTaggedUserIds())
                .build();

        notificationService.notifyTaggedUsers(notifyTaggedUsers);

        return postsService.getPostsByUser(userId);
    }

    @PatchMapping("/{postId}")
    public List<ResponsePostsDto> editPost(@LoginUser SessionUser loginUser,
                                           @PathVariable Long postId,
                                           @RequestBody RequestEditPostDto request) {
        Long loginUserId = loginUser.getId();

        String content = request.getContent();
        postsService.editPost(postId,content);

        return postsService.getPostsByUser(loginUserId);
    }

    @DeleteMapping("/{postId}")
    public List<ResponsePostsDto> deletePost(@LoginUser SessionUser loginUser,
                                             @PathVariable Long postId) {
        Long loginUserId = loginUser.getId();

        postsService.deletePost(postId);

        return postsService.getPostsByUser(loginUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likePost(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestLikePostDto request) {
        Long postId = request.getPostId();

        Long userId = loginUser.getId();
        String context = request.getContext();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();

        LikePostDto likePostDto = LikePostDto.builder()
                .user(user)
                .postId(postId)
                .writerId(postUserId)
                .build();

        postsService.likePost(likePostDto);

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
