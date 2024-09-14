package com.pigeon_stargram.sns_clone.dto.user.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseUserInfoDto {

    private Long userId;
    private String name;
    private String company;
    private String avatar;
}
