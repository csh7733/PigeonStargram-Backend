package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

import java.util.List;

/**
 * 새로운 답글을 작성하기 위한 데이터 전송 객체 (DTO)입니다.
 *
 * 이 클래스는 답글의 내용과 태그된 사용자 ID 목록을 포함하여
 * 서버로 전송할 데이터를 정의합니다.
 */
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class newReplyDto {
    private String content;
    private List<Long> taggedUserIds;
}