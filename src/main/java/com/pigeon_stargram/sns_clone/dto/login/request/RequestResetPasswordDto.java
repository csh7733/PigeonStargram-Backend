package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResetPasswordDto {
    private String token;
    private String newPassword;
}
