package com.pigeon_stargram.sns_clone.dto.Follow;

import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFollowDto {

    private Long fromId;
    private Long toId;
}
