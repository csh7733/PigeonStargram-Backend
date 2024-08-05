package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto2 {
    private String id;
    private ProfileDto2 profile;
    private ReplyDataDto2 data;
}
