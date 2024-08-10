package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.AddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.LikeReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/replies")
@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final PostsService postsService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final UserService userService;

    @PostMapping
    public List<PostsDto> addReply(@RequestBody AddReplyDto request) {
        Long commentId = request.getCommentId();
        Comment comment = commentService.getCommentEntity(commentId);
        String content = request.getReply().getContent();

        Long userId = request.getReply().getUserId();
        User user = userService.findById(userId);
        replyService.createReply(user,comment,content);
        return postsService.getAllPosts();
    }

    @PatchMapping("/{replyId}")
    public List<PostsDto> editReply(@LoginUser SessionUser loginUser,
                                    @PathVariable Long replyId,
                                    @RequestBody EditReplyDto request) {
        log.info("patch {}",replyId);
        String content = request.getContent();
        replyService.editReply(replyId,content);

        return postsService.getAllPosts();
    }
    @DeleteMapping("/{replyId}")
    public List<PostsDto> deleteReply(@LoginUser SessionUser loginUser,
                                      @PathVariable Long replyId) {
        log.info("delete {}",replyId);
        replyService.deleteReply(replyId);

        return postsService.getAllPosts();
    }

    @PostMapping("/like")
    public List<PostsDto> likeReply(@LoginUser SessionUser loginUser,
                                    @RequestBody LikeReplyDto request) {
        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long replyId = request.getReplyId();

        replyService.likeReply(user,replyId);
        return postsService.getAllPosts();
    }
}
