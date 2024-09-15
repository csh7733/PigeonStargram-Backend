package com.pigeon_stargram.sns_clone.dto.login.response;

import lombok.*;

/**
 * 사용자 이메일 정보를 담기 위한 DTO (Data Transfer Object) 클래스입니다.
 * <p>
 * 이 클래스는 사용자의 이메일 정보를 클라이언트와 서버 간에 전송하기 위해 사용됩니다.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEmailInfoDto {

    private String email;
}
