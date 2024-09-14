package com.pigeon_stargram.sns_clone.dto.Follow.request;

import lombok.*;

/**
 * 팔로워 삭제 요청을 위한 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 팔로우를 삭제할 때 필요한 정보를 담고 있습니다. 현재는 삭제할 팔로우의 사용자 ID만 포함됩니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RequestDeleteFollowerDto {
    Long id;
}
