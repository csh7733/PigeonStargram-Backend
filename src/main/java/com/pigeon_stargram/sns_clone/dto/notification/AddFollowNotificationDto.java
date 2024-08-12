package com.pigeon_stargram.sns_clone.dto.notification;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFollowNotificationDto {

    private Long senderId;
    private Long recipientId;
}
