package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.request.createPostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.LikePostDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public List<PostsDto> getPosts(@LoginUser SessionUser loginUser) {
        User user = userService.findById(loginUser.getId());

//        return postsService.getPostsByUser(user);
        return postsService.getAllPosts();
    }

    @PostMapping
    public List<PostsDto> createPosts(@LoginUser SessionUser loginUser, @RequestBody createPostDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        String content = request.getContent();
        log.info("userId = {},content = {}",userId,content);
        postsService.createPost(user,content);
        return postsService.getAllPosts();
    }

    @PatchMapping("/{postId}")
    public List<PostsDto> editPost(@PathVariable Long postId, @RequestBody EditPostDto request) {
        log.info("patch {}",postId);
        String content = request.getContent();
        postsService.editPost(postId,content);

        return postsService.getAllPosts();
    }

    @DeleteMapping("/{postId}")
    public List<PostsDto> deletePost(@PathVariable Long postId) {
        log.info("delete {}",postId);
        postsService.deletePost(postId);

        return postsService.getAllPosts();
    }

    @PostMapping("/like")
    public List<PostsDto> likePost(@LoginUser SessionUser loginUser, @RequestBody LikePostDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        postsService.likePost(user,request.getPostId());
        return postsService.getAllPosts();
    }

}
