package com.pigeon_stargram.sns_clone.dto.story.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkStoryAsViewedDto {
    private Long storyId;
    private Long userId;
}