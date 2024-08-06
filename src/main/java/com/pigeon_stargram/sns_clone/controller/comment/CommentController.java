package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.request.AddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostsService postsService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    @PostMapping
    public List<PostsDto> addComment(@RequestBody AddCommentDto request) {
        Long postId = request.getPostId();
        Posts post = postsService.getPostEntity(postId);
        String content = request.getComment().getContent();

        Long userId = request.getComment().getUserId();
        User user = userRepository.findById(userId).get();

        commentService.createComment(user,post,content);
        return postsService.getAllPosts();
    }

    @PostMapping("/like")
    public List<PostsDto> likeComment(@RequestBody LikeCommentDto request) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();
        Long commentId = request.getCommentId();

        commentService.likeComment(user,commentId);
        return postsService.getAllPosts();
    }
}
