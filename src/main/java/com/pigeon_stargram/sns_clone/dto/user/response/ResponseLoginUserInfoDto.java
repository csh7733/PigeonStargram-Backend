package com.pigeon_stargram.sns_clone.dto.user.response;

import lombok.*;

/**
 * 현재 접속중인 사용자의 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseLoginUserInfoDto {

    private Long user;          // 사용자 ID
    private String name;
    private String company;
    private String avatar;
    private boolean isLoggedIn;

}
