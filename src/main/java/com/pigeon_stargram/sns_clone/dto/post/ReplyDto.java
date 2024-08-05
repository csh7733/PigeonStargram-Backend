package com.pigeon_stargram.sns_clone.dto.post;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {
    private String id;
    private ProfileDto profile;
    private ReplyDataDto data;
}
