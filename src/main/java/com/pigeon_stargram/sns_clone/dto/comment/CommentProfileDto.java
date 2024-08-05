package com.pigeon_stargram.sns_clone.dto.comment;

import com.pigeon_stargram.sns_clone.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentProfileDto {
    private Long id;
    private String avatar;
    private String name;
    private String time;

    public CommentProfileDto(User user, LocalDateTime modifiedDate) {
        this.id = user.getId();
        this.avatar = user.getAvatar();
        this.name = user.getName();
        this.time = formatTime(modifiedDate);
    }
}