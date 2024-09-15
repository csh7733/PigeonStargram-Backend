package com.pigeon_stargram.sns_clone.dto.login.request;

import com.pigeon_stargram.sns_clone.domain.user.Role;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestRegisterDto {

    private String email;
    private String password;
    private String username;
    private String company;
    private String personalPhone;
    @Builder.Default
    private String avatar = "avatar-3.png";
}
