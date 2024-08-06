package com.pigeon_stargram.sns_clone.dto.comment.response;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
@AllArgsConstructor
public class CommentLikeDto {
    private Boolean like;
    private Integer value;

    public CommentLikeDto(Comment comment) {
        this.like = false;
        this.value = comment.getLikes();
    }
}