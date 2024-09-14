package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

/**
 * 알림 설정을 확인하기 위한 요청 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 특정 사용자의 알림 수신 여부를 확인하기 위한 정보를 담고 있습니다.
 * 주로 로그인한 사용자와 대상 사용자의 ID를 포함하여, 대상 사용자에게 알림이 활성화되어 있는지 여부를 조회합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetNotificationEnabledDto {

    private Long loginUserId;
    private Long targetUserId;
}
