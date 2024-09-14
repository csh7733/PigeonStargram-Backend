package com.pigeon_stargram.sns_clone.config.auth.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 세션으로부터 얻은 로그인 사용자 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SessionUser implements Serializable {

    private Long id;
    private String email;
    private String name;
    private String picture;

}
