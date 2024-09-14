package com.pigeon_stargram.sns_clone.dto.Follow.response;

import lombok.*;

/**
 * 팔로워 응답 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 팔로워의 정보와 관련된 데이터를 클라이언트에 전달하는 데 사용됩니다.
 * 팔로워의 기본 정보와 함께 로그인 사용자가 읽지 않은 스토리의 존재 여부를 포함합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseFollowerDto {

    private Long id;
    private String name;
    private String location;
    private String avatar;
    private Integer follow;
    private Boolean hasUnreadStories;   // 로그인 사용자가 읽지않은 스토리 존재 여부
}
