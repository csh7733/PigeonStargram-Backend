package com.pigeon_stargram.sns_clone.dto.login.request;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {
    private String email;
}
