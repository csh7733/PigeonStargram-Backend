package com.pigeon_stargram.sns_clone.dto.story.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddStoryDto {
    private String content;
}
