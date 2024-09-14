package com.pigeon_stargram.sns_clone.dto.login.request;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RequestRegisterDto {

    private String email;
    private String password;
    private String username;
    private String company;
    private String personalPhone;
    @Builder.Default
    private String avatar = "avatar-3.png";

    public User toUser(){
        return User.builder()
                .workEmail(email)
                .password(password)
                .name(username)
                .company(company)
                .personalPhone(personalPhone)
                .avatar(avatar)
                .build();
    }
}
