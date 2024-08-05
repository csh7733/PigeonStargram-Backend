package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddComment;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeComment;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post2.*;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/add")
    public List<PostsDto> addComment(@RequestBody RequestAddComment requestAddComment) {
        Long postId = requestAddComment.getPostId();
        Posts post = postsService.getPostEntity(postId);
        String content = requestAddComment.getComment().getContent();

        Long userId = requestAddComment.getComment().getUserId();
        User user = userRepository.findById(userId).get();

        commentService.createComment(user,post,content);
        return postsService.getPostsByUser(user);
    }

    @PostMapping("/list/like")
    public List<PostsDto> likeComment(@RequestBody RequestLikeComment requestLikeComment) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();
        Long commentId = requestLikeComment.getCommentId();

        commentService.likeComment(user,commentId);
        return postsService.getPostsByUser(user);
    }
}
