package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostsDto2 {
    private String id;
    private ProfileDto2 profile;
    private DataDto2 data;
}
