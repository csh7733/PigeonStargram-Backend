package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPost;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePost;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
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

    @PatchMapping("/{postId}")
    public List<PostsDto> editPost(@PathVariable Long postId, @RequestBody RequestEditPost request) {
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
    public List<PostsDto> likePost(@RequestBody RequestLikePost request) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();

        postsService.likePost(user,request.getPostId());
        return postsService.getAllPosts();
    }

}
