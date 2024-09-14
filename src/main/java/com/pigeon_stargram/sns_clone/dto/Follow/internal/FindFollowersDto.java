package com.pigeon_stargram.sns_clone.dto.Follow.internal;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FindFollowersDto {

    private Long loginUserId;
    private Long userId;
}
