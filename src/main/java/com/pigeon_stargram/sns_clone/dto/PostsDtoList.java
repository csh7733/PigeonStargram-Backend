package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsDtoList {
    private List<PostsDto> posts;
}
