package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AddFollowerDto {
    String id;

    public Follow toEntity(User toUser, User fromUser) {
        return new Follow(fromUser, toUser);
    }
}
