package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.EditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestDeleteReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestEditReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudServiceV2;
import com.pigeon_stargram.sns_clone.service.reply.ReplyCrudServiceV2;
import com.pigeon_stargram.sns_clone.service.reply.ReplyServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.pigeon_stargram.sns_clone.dto.reply.ReplyDtoConverter.*;
import static com.pigeon_stargram.sns_clone.util.LogUtil.logControllerMethod;

/**
 * ReplyController는 답글과 관련된 API 요청을 처리하는 컨트롤러입니다.
 * <p>
 * 답글 생성, 수정, 삭제, 좋아요 관련 API를 제공하며, 각 요청에 대해 필요한 서비스 계층과의 통신을 담당합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/replies")
@RequiredArgsConstructor
@Slf4j
public class ReplyController {

    private final ReplyServiceV2 replyService;
    private final CommentCrudServiceV2 commentCrudService;
    private final ReplyCrudServiceV2 replyCrudService;

    /**
     * 답글을 생성하는 메서드입니다.
     * <p>
     * 요청된 데이터를 기반으로 새로운 답글을 생성하고, 생성된 답글 정보를 반환합니다.
     * </p>
     *
     * @param loginUser 현재 로그인한 사용자 정보
     * @param request   새로운 답글 요청 데이터를 담고 있는 DTO
     * @return 생성된 답글의 정보를 담은 ResponseReplyDto 객체
     */
    @PostMapping
    public ResponseReplyDto addReply(@LoginUser SessionUser loginUser,
                                     @RequestBody RequestAddReplyDto request) {
        logControllerMethod("addReply", loginUser, request);

        // 요청된 댓글 ID를 이용해 댓글 작성자 정보를 가져옴
        Long commentId = request.getCommentId();
        Long commentUserId = commentCrudService.findById(commentId).getUser().getId();

        CreateReplyDto dto = toCreateReplyDto(request, loginUser, commentUserId);
        return replyService.createReply(dto);
    }

    /**
     * 답글을 수정하는 메서드입니다.
     * <p>
     * 답글 ID와 수정된 내용을 기반으로 답글을 업데이트합니다.
     * </p>
     *
     * @param loginUser 현재 로그인한 사용자 정보
     * @param replyId   수정하려는 답글의 ID
     * @param request   수정된 답글 내용을 담고 있는 DTO
     */
    @PatchMapping("/{replyId}")
    public void editReply(@LoginUser SessionUser loginUser,
                          @PathVariable Long replyId,
                          @RequestBody RequestEditReplyDto request) {
        logControllerMethod("editReply", loginUser, replyId, request);

        EditReplyDto dto = toEditReplyDto(request, replyId);
        replyService.editReply(dto);
    }

    /**
     * 답글을 삭제하는 메서드입니다.
     * <p>
     * 답글 ID를 기반으로 해당 답글을 삭제합니다.
     * </p>
     *
     * @param loginUser 현재 로그인한 사용자 정보
     * @param replyId   삭제하려는 답글의 ID
     * @param request   삭제 요청 데이터를 담고 있는 DTO
     */
    @DeleteMapping("/{replyId}")
    public void deleteReply(@LoginUser SessionUser loginUser,
                            @PathVariable Long replyId,
                            @RequestBody RequestDeleteReplyDto request) {
        logControllerMethod("deleteReply", loginUser, replyId, request);

        replyCrudService.deleteById(replyId);
    }

    /**
     * 답글에 좋아요를 추가하거나 삭제하는 메서드입니다.
     * <p>
     * 요청된 답글에 대해 좋아요를 추가하거나, 이미 좋아요가 되어 있으면 삭제합니다.
     * </p>
     *
     * @param loginUser 현재 로그인한 사용자 정보
     * @param request   좋아요 요청 데이터를 담고 있는 DTO
     * @return 좋아요 처리 결과 (true: 좋아요 추가, false: 좋아요 삭제)
     */
    @PostMapping("/like")
    public Boolean likeReply(@LoginUser SessionUser loginUser,
                             @RequestBody RequestLikeReplyDto request) {
        logControllerMethod("likeReply", loginUser, request);

        LikeReplyDto dto = toLikeReplyDto(request, loginUser);
        return replyService.likeReply(dto);
    }
}

