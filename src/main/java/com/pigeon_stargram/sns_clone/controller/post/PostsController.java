package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
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
    private final UserService userService;

    @GetMapping
    public List<ResponsePostsDto> getPosts(@RequestParam Long userId) {
        User user = userService.findById(userId);

        return postsService.getPostsByUser(user);
    }

    @PostMapping
    public ResponsePostsDto createPosts(@LoginUser SessionUser loginUser,
                                        @RequestBody RequestCreatePostDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        String content = request.getContent();

        Posts post = postsService.createPost(new CreatePostDto(user, content));

        return postsService.getPostById(post.getId());
    }

    @PatchMapping("/{postId}")
    public List<ResponsePostsDto> editPost(@LoginUser SessionUser loginUser,
                                           @PathVariable Long postId,
                                           @RequestBody RequestEditPostDto request) {

        String content = request.getContent();
        postsService.editPost(postId,content);

        return postsService.getAllPosts();
    }

    @DeleteMapping("/{postId}")
    public List<ResponsePostsDto> deletePost(@LoginUser SessionUser loginUser,
                                             @PathVariable Long postId) {
        log.info("delete {}",postId);
        postsService.deletePost(postId);

        return postsService.getAllPosts();
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likePost(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestLikePostDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        postsService.likePost(new LikePostDto(user, request.getPostId()));
        return postsService.getAllPosts();
    }

}
