package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFollowDto {

    private Long senderId;
    private Long recipientId;
}
