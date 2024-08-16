package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestDeleteReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
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
    private final BasicUserService userService;

    @PostMapping
    public List<ResponsePostsDto> addReply(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestAddReplyDto request) {
        Long postId = request.getPostId();

        Long commentId = request.getCommentId();
        Comment comment = commentService.getCommentEntity(commentId);
        String content = request.getReply().getContent();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        CreateReplyDto createReplyDto = CreateReplyDto.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        replyService.createReply(createReplyDto);


        return postsService.getPostsByUser(postUser);
    }

    @PatchMapping("/{replyId}")
    public List<ResponsePostsDto> editReply(@LoginUser SessionUser loginUser,
                                            @PathVariable Long replyId,
                                            @RequestBody RequestEditReplyDto request) {
        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        String content = request.getContent();
        replyService.editReply(replyId,content);

        return postsService.getPostsByUser(postUser);
    }
    @DeleteMapping("/{replyId}")
    public List<ResponsePostsDto> deleteReply(@LoginUser SessionUser loginUser,
                                              @PathVariable Long replyId,
                                              @RequestBody RequestDeleteReplyDto request) {
        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        replyService.deleteReply(replyId);

        return postsService.getPostsByUser(postUser);
    }

    @PostMapping("/like")
    public List<ResponsePostsDto> likeReply(@LoginUser SessionUser loginUser,
                                            @RequestBody RequestLikeReplyDto request) {
        Long postId = request.getPostId();

        Long userId = loginUser.getId();
        User user = userService.findById(userId);

        Long postUserId = request.getPostUserId();
        User postUser = userService.findById(postUserId);

        Long replyId = request.getReplyId();

        LikeReplyDto likeReplyDto = LikeReplyDto.builder()
                .user(user)
                .replyId(replyId)
                .postUserId(postUserId)
                .postId(postId)
                .build();

        replyService.likeReply(likeReplyDto);

        return postsService.getPostsByUser(postUser);
    }
}
