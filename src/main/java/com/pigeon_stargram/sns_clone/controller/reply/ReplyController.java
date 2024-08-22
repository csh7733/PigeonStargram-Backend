package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestDeleteReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
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
    private final TimelineService timelineService;
    private final ReplyService replyService;
    private final UserService userService;
    private final CommentCrudService commentCrudService;
    private final ReplyCrudService replyCrudService;

    @PostMapping
    public List<ResponsePostDto> addReply(@LoginUser SessionUser loginUser,
                                          @RequestBody RequestAddReplyDto request) {
        Long userId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        Long commentId = request.getCommentId();
        Long commentUserId = commentCrudService.findById(commentId).getUser().getId();

        CreateReplyDto createReplyDto = buildCreateReplyDto(request, loginUser, commentUserId);
        replyService.createReply(createReplyDto);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @PatchMapping("/{replyId}")
    public List<ResponsePostDto> editReply(@LoginUser SessionUser loginUser,
                                           @PathVariable Long replyId,
                                           @RequestBody RequestEditReplyDto request) {
        Long userId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        EditReplyDto editReplyDto = buildEditReplyDto(request, replyId);
        replyService.editReply(editReplyDto);

        return getPostsBasedOnContext(context, userId, postUserId);
    }
    @DeleteMapping("/{replyId}")
    public List<ResponsePostDto> deleteReply(@LoginUser SessionUser loginUser,
                                             @PathVariable Long replyId,
                                             @RequestBody RequestDeleteReplyDto request) {
        Long userId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        replyCrudService.deleteById(replyId);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostDto> likeReply(@LoginUser SessionUser loginUser,
                                           @RequestBody RequestLikeReplyDto request) {
        Long userId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        LikeReplyDto likeReplyDto = buildLikeReplyDto(request, loginUser);
        replyService.likeReply(likeReplyDto);

        return getPostsBasedOnContext(context, userId, postUserId);
    }

    private List<ResponsePostDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if ("timeline".equals(context)) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postService.getPostsByUserId(postUserId);
        }
    }
}
