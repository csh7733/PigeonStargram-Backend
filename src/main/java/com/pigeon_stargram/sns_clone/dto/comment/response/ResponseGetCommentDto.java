package com.pigeon_stargram.sns_clone.dto.comment.response;

import lombok.*;

import java.util.List;

/**
 * 댓글 목록 조회 응답을 위한 DTO 클래스입니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseGetCommentDto {

    private List<ResponseCommentDto> comments;
    private Boolean isMoreComments; // 추가로 불러올 댓글이 있는지 여부
}
