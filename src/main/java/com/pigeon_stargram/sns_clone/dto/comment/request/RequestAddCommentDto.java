package com.pigeon_stargram.sns_clone.dto.comment.request;


import lombok.*;

/**
 * 댓글 추가 요청을 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 댓글을 추가할 게시물에 대한 정보와 댓글의 내용을 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddCommentDto {

    private Long postId;
    private Long postUserId;
    private String context;
    private newCommentDto comment;
}
