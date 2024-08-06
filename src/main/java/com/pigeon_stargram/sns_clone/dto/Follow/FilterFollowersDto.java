package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterFollowersDto {

    private Long userId;
    private String key;

    public FilterFollowersDto(User user, RequestFilterFollowersDto dto){
        this.userId = user.getId();
        this.key = dto.getKey();
    }
}
