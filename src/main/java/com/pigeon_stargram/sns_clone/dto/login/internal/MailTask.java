package com.pigeon_stargram.sns_clone.dto.login.internal;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailTask {
    private String email;
    private String subject;
    private String body;
}
