package com.pigeon_stargram.sns_clone.dto.post;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String img;
    private Boolean featured;

    public ImageDto(Image image) {
        this.img = image.getImg();
        this.featured = image.getFeatured();
    }

}
