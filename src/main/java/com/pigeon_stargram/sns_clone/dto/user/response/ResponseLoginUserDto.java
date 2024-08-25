package com.pigeon_stargram.sns_clone.dto.user.response;

import lombok.*;
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLoginUserDto {
    private Boolean isLoggedIn;
    private Long user;
    private String name;
    private String company;
    private String avatar;
}
