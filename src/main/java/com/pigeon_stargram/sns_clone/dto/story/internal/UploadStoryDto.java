// RequestUploadStoryDto.java
package com.pigeon_stargram.sns_clone.dto.story.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadStoryDto {
    private Long userId;
    private String content;
    private String imageUrl;
}
