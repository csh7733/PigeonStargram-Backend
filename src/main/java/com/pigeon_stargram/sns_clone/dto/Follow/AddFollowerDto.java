package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFollowerDto {
    private Long fromId;
    private Long toId;

    public Follow toEntity(User fromUser, User toUser){
        return Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }
}
