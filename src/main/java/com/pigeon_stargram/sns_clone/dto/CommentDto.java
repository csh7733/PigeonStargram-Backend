package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String id;
    private ProfileDto profile;
    private CommentDataDto data;
}
