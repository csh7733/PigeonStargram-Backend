package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

/**
 * 팔로우 삭제 요청에 필요한 정보를 담고 있는 DTO 클래스입니다.
 *
 * 이 클래스는 팔로우 관계를 삭제할 때 필요한 발신자와 수신자 정보를 포함합니다.
 * 팔로우를 삭제하기 위한 요청 데이터 전송에 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeleteFollowDto {

    private Long senderId;
    private Long recipientId;
}
