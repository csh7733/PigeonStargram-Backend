package com.pigeon_stargram.sns_clone.dto.story.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRecentStoriesDto {
    private Long userId;
    private Long currentMemberId;
}