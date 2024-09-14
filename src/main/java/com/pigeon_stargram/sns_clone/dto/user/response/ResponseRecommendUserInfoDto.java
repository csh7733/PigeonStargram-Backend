package com.pigeon_stargram.sns_clone.dto.user.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseRecommendUserInfoDto {

    private Long userId;
    private String name;
    private String avatar;
    private String company;
    private String randomRecommendName;
    private Integer total;
}
