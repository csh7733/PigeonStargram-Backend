package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetNotificationEnabledDto {

    private Long loginUserId;
    private Long targetUserId;
}
