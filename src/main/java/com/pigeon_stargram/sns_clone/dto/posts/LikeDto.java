package com.pigeon_stargram.sns_clone.dto.posts;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private boolean like;
    private int value;
}
