package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindFollowingsDto {

    private Long loginUserId;
    private Long userId;
}
