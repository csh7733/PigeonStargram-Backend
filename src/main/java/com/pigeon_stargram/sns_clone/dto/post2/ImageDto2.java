package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto2 {
    private String img;
    private String title;
    private boolean featured;
}
