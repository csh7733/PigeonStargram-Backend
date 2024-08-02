package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private String id;
    private String avatar;
    private String name;
    private String time;
}
