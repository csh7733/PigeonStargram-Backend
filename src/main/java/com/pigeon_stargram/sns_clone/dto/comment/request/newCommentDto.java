package com.pigeon_stargram.sns_clone.dto.comment.request;

import lombok.*;

import java.util.List;

/**
 * 새 댓글 작성을 위한 데이터 전송 객체(DTO)입니다.
 * <p>
 * 이 클래스는 새 댓글의 내용과 태그된 사용자 ID 목록을 포함합니다.
 * </p>
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class newCommentDto {
    private String content;
    private List<Long> taggedUserIds;
}