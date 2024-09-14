package com.pigeon_stargram.sns_clone.dto.Follow.request;

import lombok.*;

/**
 * 팔로워 추가 요청을 위한 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 팔로우 요청 시 필요한 정보를 담고 있습니다. 현재는 팔로우를 추가할 사용자 ID만 포함됩니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RequestAddFollowerDto {

    Long id;
}
