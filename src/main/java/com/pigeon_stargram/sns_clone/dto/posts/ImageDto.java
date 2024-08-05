package com.pigeon_stargram.sns_clone.dto.posts;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String img;
    private String title;
    private boolean featured;
}
