package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.request.createPostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.LikePostDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
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

    @GetMapping
    public List<PostsDto> getPosts(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
            return postsService.getPostsByUser(user);
        } else {
            return postsService.getAllPosts();
        }
    }

    @PostMapping
    public List<PostsDto> createPosts(@RequestBody createPostDto request) {
        Long userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

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
    public List<PostsDto> likePost(@RequestBody LikePostDto request) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();

        postsService.likePost(user,request.getPostId());
        return postsService.getAllPosts();
    }

}
