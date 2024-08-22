package com.pigeon_stargram.sns_clone.dto.user.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    private Long userId;
    private String password;
}
