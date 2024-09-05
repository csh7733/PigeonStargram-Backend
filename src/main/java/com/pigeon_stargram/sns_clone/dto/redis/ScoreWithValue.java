package com.pigeon_stargram.sns_clone.dto.redis;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreWithValue<T> {
    private T value;
    private Double score;
}
