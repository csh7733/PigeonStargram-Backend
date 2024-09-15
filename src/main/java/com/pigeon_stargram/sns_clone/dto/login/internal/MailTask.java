package com.pigeon_stargram.sns_clone.dto.login.internal;

import lombok.*;

/**
 * 이메일 발송 작업을 나타내는 DTO 클래스입니다.
 * 이메일 수신자, 제목, 본문을 포함한 메일 발송 작업의 세부 정보를 담고 있습니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MailTask {

    private String email;   // 이메일 수신자의 주소
    private String subject; // 이메일 제목
    private String body;    // 이메일 본문
}
