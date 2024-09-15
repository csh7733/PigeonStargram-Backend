package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestDeleteReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.reply.ReplyBuilder.*;

@Slf4j
@RequestMapping("/api/replies")
@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final PostService postService;
    private final TimelineServiceV2 timelineService;
    private final ReplyService replyService;
    private final CommentCrudService commentCrudService;
    private final ReplyCrudService replyCrudService;

    @PostMapping
    public ResponseReplyDto addReply(@LoginUser SessionUser loginUser,
                                     @RequestBody RequestAddReplyDto request) {
        Long commentId = request.getCommentId();
        Long commentUserId = commentCrudService.findById(commentId).getUser().getId();

        CreateReplyDto createReplyDto = buildCreateReplyDto(request, loginUser, commentUserId);

        return replyService.createReply(createReplyDto);
    }

    @PatchMapping("/{replyId}")
    public void editReply(@LoginUser SessionUser loginUser,
                          @PathVariable Long replyId,
                          @RequestBody RequestEditReplyDto request) {
        EditReplyDto editReplyDto = buildEditReplyDto(request, replyId);

        replyService.editReply(editReplyDto);
    }

    @DeleteMapping("/{replyId}")
    public void deleteReply(@LoginUser SessionUser loginUser,
                            @PathVariable Long replyId,
                            @RequestBody RequestDeleteReplyDto request) {

        replyCrudService.deleteById(replyId);
    }

    @PostMapping("/like")
    public Boolean likeReply(@LoginUser SessionUser loginUser,
                             @RequestBody RequestLikeReplyDto request) {
        LikeReplyDto likeReplyDto = buildLikeReplyDto(request, loginUser);

        return replyService.likeReply(likeReplyDto);
    }

    private List<ResponsePostDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if ("timeline".equals(context)) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postService.getPostsByUserId(postUserId);
        }
    }
}
