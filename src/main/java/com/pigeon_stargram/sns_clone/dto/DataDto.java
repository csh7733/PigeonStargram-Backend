package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    private String content;
    private List<ImageDto> images;
    private LikeDto likes;
    private List<CommentDto> comments;
    private String video;
}
