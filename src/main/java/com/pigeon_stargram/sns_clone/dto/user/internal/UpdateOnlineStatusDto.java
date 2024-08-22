package com.pigeon_stargram.sns_clone.dto.user.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOnlineStatusDto {
    private Long userId;
    private String onlineStatus;
}
