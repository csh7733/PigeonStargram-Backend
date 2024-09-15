package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.*;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.post.PostServiceV2;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.service.comment.CommentBuilder.*;

@Slf4j
@RequestMapping("/api/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final PostService postService;
    private final TimelineService timelineService;
    private final CommentService commentService;

    @GetMapping
    public ResponseGetCommentDto getComment(@LoginUser SessionUser loginUser,
                                            @RequestParam Long postId,
                                            @RequestParam Long lastCommentId) {

        RequestGetCommentDto request = RequestGetCommentDto.builder()
                .postId(postId)
                .lastCommentId(lastCommentId)
                .build();

        return commentService.getPartialComment(request);
    }

    @PostMapping
    public ResponseCommentDto addComment(@LoginUser SessionUser loginUser,
                                         @RequestBody RequestAddCommentDto request) {

        CreateCommentDto createCommentDto = buildCreateCommentDto(request, loginUser);

        return commentService.createComment(createCommentDto);
    }

    @PatchMapping("/{commentId}")
    public List<ResponsePostDto> editComment(@LoginUser SessionUser loginUser,
                                             @PathVariable Long commentId,
                                             @RequestBody RequestEditCommentDto request) {

        Long loginUserId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        EditCommentDto editCommentDto = buildEditCommentDto(commentId, request.getContent());
        commentService.editComment(editCommentDto);

        return getPostsBasedOnContext(context, loginUserId, postUserId);
    }

    @DeleteMapping("/{commentId}")
    public List<ResponsePostDto> deleteComment(@LoginUser SessionUser loginUser,
                                               @PathVariable Long commentId,
                                               @RequestBody RequestDeleteCommentDto request) {
        Long loginUserId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        commentService.deleteComment(commentId);

        return getPostsBasedOnContext(context, loginUserId, postUserId);
    }

    @PostMapping("/like")
    public Boolean likeComment(@LoginUser SessionUser loginUser,
                               @RequestBody RequestLikeCommentDto request) {
        LikeCommentDto likeCommentDto = buildLikeCommentDto(request, loginUser);

        return commentService.likeComment(likeCommentDto);
    }

    private List<ResponsePostDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if (context.equals("timeline")) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postService.getPostsByUserId(postUserId);
        }
    }
}
