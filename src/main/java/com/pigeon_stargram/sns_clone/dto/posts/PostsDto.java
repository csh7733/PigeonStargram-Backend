package com.pigeon_stargram.sns_clone.dto.posts;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsDto {
    private String id;
    private ProfileDto profile;
    private DataDto data;
}
