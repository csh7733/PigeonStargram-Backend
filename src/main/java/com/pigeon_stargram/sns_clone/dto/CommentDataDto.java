package com.pigeon_stargram.sns_clone.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDataDto {
    private String comment;
    private LikeDto likes;
    private List<ReplyDto> replies;
}
