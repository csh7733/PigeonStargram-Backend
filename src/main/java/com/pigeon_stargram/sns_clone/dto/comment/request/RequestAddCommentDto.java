package com.pigeon_stargram.sns_clone.dto.comment.request;


import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAddCommentDto {
    private Long postId;
    private Long postUserId;
    private String context;
    private newCommentDto comment;
}
