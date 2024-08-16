package com.pigeon_stargram.sns_clone.dto.login.request;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLoginDto {
    private String email;
    private String password;
}
