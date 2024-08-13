package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestEditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
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

    @PostMapping
    public List<PostsDto> addComment(@LoginUser SessionUser loginUser,
                                     @RequestBody RequestAddCommentDto request) {
        Long postId = request.getPostId();
        Posts post = postsService.getPostEntity(postId);
        String content = request.getComment().getContent();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        commentService.createComment(new CreateCommentDto(user, post, content));
        return postsService.getAllPosts();
    }

    @PatchMapping("/{commentId}")
    public List<PostsDto> editComment(@LoginUser SessionUser loginUser,
                                      @PathVariable Long commentId,
                                      @RequestBody RequestEditCommentDto request) {
        log.info("patch {}",commentId);
        String content = request.getContent();
        commentService.editComment(commentId,content);

        return postsService.getAllPosts();
    }

    @DeleteMapping("/{commentId}")
    public List<PostsDto> deleteComment(@LoginUser SessionUser loginUser,
                                        @PathVariable Long commentId) {
        log.info("delete {}",commentId);
        commentService.deleteComment(commentId);

        return postsService.getAllPosts();
    }

    @PostMapping("/like")
    public List<PostsDto> likeComment(@LoginUser SessionUser loginUser,
                                      @RequestBody RequestLikeCommentDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long commentId = request.getCommentId();

        commentService.likeComment(new LikeCommentDto(user, commentId));
        return postsService.getAllPosts();
    }
}
