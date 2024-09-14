package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestResetPasswordDto {

    private String token;
    private String newPassword;
}
