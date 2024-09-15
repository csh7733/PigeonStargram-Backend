package com.pigeon_stargram.sns_clone.dto.comment;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestAddCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestGetCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.request.RequestLikeCommentDto;

public class CommentDtoConverter {

    public static RequestGetCommentDto toRequestGetCommentDto(Long postId,
                                                              Long lastCommentId) {
        return RequestGetCommentDto.builder()
                .postId(postId)
                .lastCommentId(lastCommentId)
                .build();
    }

    public static CreateCommentDto toCreateCommentDto(RequestAddCommentDto dto,
                                                      SessionUser loginUser) {
        return CreateCommentDto.builder()
                .loginUserId(loginUser.getId())
                .postId(dto.getPostId())
                .postUserId(dto.getPostUserId())
                .context(dto.getContext())
                .content(dto.getComment().getContent())
                .taggedUserIds(dto.getComment().getTaggedUserIds())
                .build();
    }

    public static EditCommentDto toEditCommentDto(Long commentId,
                                                  String content) {
        return EditCommentDto.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }

    public static LikeCommentDto toLikeCommentDto(RequestLikeCommentDto dto,
                                                  SessionUser loginUser) {
        return LikeCommentDto.builder()
                .loginUserId(loginUser.getId())
                .postUserId(dto.getPostUserId())
                .commentId(dto.getCommentId())
                .postId(dto.getPostId())
                .build();
    }
}
