package com.pigeon_stargram.sns_clone.dto.post2;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDataDto2 {
    private String comment;
    private LikeDto2 likes;
    private List<ReplyDto2> replies;
}
