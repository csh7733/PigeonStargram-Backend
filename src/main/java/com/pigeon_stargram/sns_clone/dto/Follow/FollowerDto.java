package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerDto {

    private Long id;
    private String avatar;
    private String name;
    private String location;
    private Integer follow;

    public FollowerDto(User user, Integer follow) {
        this.id = user.getId();
        this.avatar = user.getAvatar();
        this.name = user.getName();
        this.location = user.getLocation();
        this.follow = follow;
    }

    // temporary
    public User toUser() {
        return User.builder()
                .id(this.id)
                .avatar(this.avatar)
                .name(this.name)
                .location(this.location)
                .build();
    }

    // temporary
    public Follow toEntity(User user) {
        return Follow.builder()
                .sender(user)
                .recipient(user)
                .build();
    }
}
