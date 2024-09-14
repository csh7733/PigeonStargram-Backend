package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

/**
 * 팔로잉을 찾기 위한 요청 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 특정 사용자의 팔로잉 목록을 찾기 위한 요청을 처리할 때 필요한 정보를 담고 있습니다.
 * 주로 로그인한 사용자와 검색하려는 대상 사용자의 정보를 포함합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FindFollowingsDto {

    private Long loginUserId;
    private Long userId;
}
