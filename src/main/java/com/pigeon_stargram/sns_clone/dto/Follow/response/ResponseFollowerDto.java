package com.pigeon_stargram.sns_clone.dto.Follow.response;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseFollowerDto {

    private Long id;
    private String avatar;
    private String name;
    private String location;
    private Integer follow;
    private boolean hasUnreadStories;

    public void setHasUnreadStories(boolean hasUnreadStories) {
        this.hasUnreadStories = hasUnreadStories;
    }

    public ResponseFollowerDto(User user, Integer follow) {
        this.id = user.getId();
        this.avatar = user.getAvatar();
        this.name = user.getName();
        this.location = user.getCompany();
        this.follow = follow;
    }
}
