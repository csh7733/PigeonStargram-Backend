package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto2 {
    private String id;
    private String avatar;
    private String name;
    private String time;
}
