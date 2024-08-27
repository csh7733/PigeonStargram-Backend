package com.pigeon_stargram.sns_clone.controller.comment;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestDeleteCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestEditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudService;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.timeline.TimelineService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
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
    private final CommentCrudService commentCrudService;

    @PostMapping
    public List<ResponsePostDto> addComment(@LoginUser SessionUser loginUser,
                                            @RequestBody RequestAddCommentDto request) {

        String context = request.getContext();
        Long loginUserId = loginUser.getId();
        Long postUserId = request.getPostUserId();

        CreateCommentDto createCommentDto = buildCreateCommentDto(request, loginUser);
        commentService.createComment(createCommentDto);

        return getPostsBasedOnContext(context, loginUserId, postUserId);
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

        commentCrudService.deleteById(commentId);

        return getPostsBasedOnContext(context, loginUserId, postUserId);
    }

    @PostMapping("/like")
    public List<ResponsePostDto> likeComment(@LoginUser SessionUser loginUser,
                                             @RequestBody RequestLikeCommentDto request) {
        Long loginUserId = loginUser.getId();
        Long postUserId = request.getPostUserId();
        String context = request.getContext();

        LikeCommentDto likeCommentDto = buildLikeCommentDto(request, loginUser);
        commentService.likeComment(likeCommentDto);

        return getPostsBasedOnContext(context, loginUserId, postUserId);
    }

    private List<ResponsePostDto> getPostsBasedOnContext(String context, Long userId, Long postUserId) {
        if (context.equals("timeline")) {
            return timelineService.getFollowingUsersRecentPosts(userId);
        } else {
            return postService.getPostsByUserId(postUserId);
        }
    }
}
