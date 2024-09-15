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
import com.pigeon_stargram.sns_clone.service.comment.CommentServiceV2;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.comment.CommentDtoConverter.*;

/**
 * 댓글 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * 특정 게시물의 댓글을 가져옵니다.
     *
     * @param loginUser     로그인한 사용자 세션 정보
     * @param postId        게시물 ID
     * @param lastCommentId 마지막 댓글 ID
     * @return 댓글 목록 응답 객체
     */
    @GetMapping
    public ResponseGetCommentDto getComment(@LoginUser SessionUser loginUser,
                                            @RequestParam Long postId,
                                            @RequestParam Long lastCommentId) {
        LogUtil.logControllerMethod("getComment", loginUser, postId, lastCommentId);

        RequestGetCommentDto dto = toRequestGetCommentDto(postId, lastCommentId);

        return commentService.getPartialComment(dto);
    }

    /**
     * 댓글을 추가합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param request   댓글 추가 요청 객체
     * @return 추가된 댓글 응답 객체
     */
    @PostMapping
    public ResponseCommentDto addComment(@LoginUser SessionUser loginUser,
                                         @RequestBody RequestAddCommentDto request) {
        LogUtil.logControllerMethod("addComment", loginUser, request);

        CreateCommentDto dto = toCreateCommentDto(request, loginUser);

        return commentService.createComment(dto);
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param commentId 수정할 댓글 ID
     * @param request   댓글 수정 요청 객체
     * @return 수정된 댓글이 포함된 게시물 목록 응답 객체
     */
    @PatchMapping("/{commentId}")
    public List<ResponsePostDto> editComment(@LoginUser SessionUser loginUser,
                                             @PathVariable Long commentId,
                                             @RequestBody RequestEditCommentDto request) {
        LogUtil.logControllerMethod("editComment", loginUser, commentId, request);

        EditCommentDto dto = toEditCommentDto(commentId, request.getContent());
        commentService.editComment(dto);

        return postService.getPostsByUserId(request.getPostUserId());
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param commentId 삭제할 댓글 ID
     * @param request   댓글 삭제 요청 객체
     * @return 삭제된 댓글이 포함된 게시물 목록 응답 객체
     */
    @DeleteMapping("/{commentId}")
    public List<ResponsePostDto> deleteComment(@LoginUser SessionUser loginUser,
                                               @PathVariable Long commentId,
                                               @RequestBody RequestDeleteCommentDto request) {
        LogUtil.logControllerMethod("deleteComment", loginUser, commentId, request);

        commentService.deleteComment(commentId);

        return postService.getPostsByUserId(request.getPostUserId());
    }

    /**
     * 댓글에 좋아요를 추가하거나 제거합니다.
     *
     * @param loginUser 로그인한 사용자 세션 정보
     * @param request   댓글 좋아요 요청 객체
     * @return 좋아요 결과
     */
    @PostMapping("/like")
    public Boolean likeComment(@LoginUser SessionUser loginUser,
                               @RequestBody RequestLikeCommentDto request) {
        LogUtil.logControllerMethod("deleteComment", loginUser, request);

        LikeCommentDto dto = toLikeCommentDto(request, loginUser);
        return commentService.likeComment(dto);
    }
}
