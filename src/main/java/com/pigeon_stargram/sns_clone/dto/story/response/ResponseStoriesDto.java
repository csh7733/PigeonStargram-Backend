package com.pigeon_stargram.sns_clone.dto.story.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStoriesDto {
    private List<ResponseStoryDto> stories;
    private Integer lastRead;
}
