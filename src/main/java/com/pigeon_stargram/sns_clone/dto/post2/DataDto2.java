package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDto2 {
    private String content;
    private List<ImageDto2> images;
    private LikeDto2 likes;
    private List<CommentDto2> comments;
    private String video;
}
