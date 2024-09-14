package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

/**
 * 알림 수신 설정을 토글하기 위한 요청 데이터 전송 객체(DTO)입니다.
 *
 * 이 클래스는 특정 사용자의 알림 수신 설정을 변경하기 위한 정보를 담고 있습니다.
 * 주로 로그인한 사용자와 대상 사용자의 ID를 포함하여, 대상 사용자에 대한 알림 수신 여부를 토글합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ToggleNotificationEnabledDto {

    private Long loginUserId;
    private Long targetUserId;
}
