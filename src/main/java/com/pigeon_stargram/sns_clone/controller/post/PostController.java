package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.*;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final TimelineService timelineService;
    private final FileService fileService;

    @GetMapping
    public List<ResponsePostDto> getPosts(@RequestParam Long userId) {
        return postService.getPostsByUserId(userId);
    }

    @PostMapping
    public Long createPosts(@LoginUser SessionUser loginUser,
                            @ModelAttribute RequestCreatePostDto request,
                            @RequestPart(value = "images", required = false)
                                List<MultipartFile> imagesFile) {
        Long userId = loginUser.getId();

        FileUploadResultDto result = fileService.saveFiles(imagesFile);

        List<String> images = result.getFileNames();
        String fieldKey = result.getFieldKey();

        CreatePostDto createPostDto = buildCreatePostDto(request, loginUser, images, fieldKey);

        return postService.createPost(createPostDto);
    }

    @PatchMapping("/{postId}")
    public List<ResponsePostDto> editPost(@LoginUser SessionUser loginUser,
                                          @PathVariable Long postId,
                                          @RequestBody RequestEditPostDto request) {
        Long loginUserId = loginUser.getId();

        EditPostDto editPostDto = buildEditPostDto(request, postId);
        postService.editPost(editPostDto);

        return postService.getPostsByUserId(loginUserId);
    }

    @DeleteMapping("/{postId}")
    public List<ResponsePostDto> deletePost(@LoginUser SessionUser loginUser,
                                            @PathVariable Long postId) {
        Long loginUserId = loginUser.getId();

        postService.deletePost(postId);

        return postService.getPostsByUserId(loginUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostDto> likePost(@LoginUser SessionUser loginUser,
                                          @RequestBody RequestLikePostDto request) {
        Long userId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        LikePostDto likePostDto = buildLikePostDto(request, loginUser);
        postService.likePost(likePostDto);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    private List<ResponsePostDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if ("timeline".equals(context)) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postService.getPostsByUserId(postUserId);
        }
    }

}
