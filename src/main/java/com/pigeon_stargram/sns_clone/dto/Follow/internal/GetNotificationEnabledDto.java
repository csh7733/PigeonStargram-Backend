package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetNotificationEnabledDto {

    private Long loginUserId;
    private Long targetUserId;
}
