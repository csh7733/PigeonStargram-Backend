package com.pigeon_stargram.sns_clone.dto.Follow.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseFollowerDto {

    private Long id;
    private String name;
    private String location;
    private String avatar;
    private Integer follow;
    private Boolean hasUnreadStories;   // 로그인 사용자가 읽지않은 스토리 존재 여부
}
