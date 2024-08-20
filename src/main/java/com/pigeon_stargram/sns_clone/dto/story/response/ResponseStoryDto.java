package com.pigeon_stargram.sns_clone.dto.story.response;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStoryDto {
    private Long id;
    private String content;
    private String img;
    private String time;

    public ResponseStoryDto(Story story) {
        this.id = story.getId();
        this.content = story.getContent();
        this.img = story.getImg();
        this.time = formatTime(story.getCreatedDate());
    }
}
