package com.pigeon_stargram.sns_clone.dto.login.request;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String email;
    private String username;
    private String company;
    private String personalPhone;
    private String password;
    @Builder.Default
    private String avatar = "avatar-3.png";

    public User toEntity(){
        return User.builder()
                .workEmail(email)
                .name(username)
                .avatar(avatar)
                .personalPhone(personalPhone)
                .company(company)
                .password(password)
                .build();
    }
}
