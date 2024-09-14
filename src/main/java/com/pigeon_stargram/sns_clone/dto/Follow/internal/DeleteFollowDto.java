package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeleteFollowDto {

    private Long senderId;
    private Long recipientId;
}
