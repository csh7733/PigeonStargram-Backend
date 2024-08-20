package com.pigeon_stargram.sns_clone.dto.story.response;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStoryDto {
    private Long id;
    private String content;
    private String img;

    public ResponseStoryDto(Story story) {
        this.id = story.getId();
        this.content = story.getContent();
        this.img = story.getImg();
    }
}
