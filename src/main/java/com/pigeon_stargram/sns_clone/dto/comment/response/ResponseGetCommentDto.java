package com.pigeon_stargram.sns_clone.dto.comment.response;

import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetCommentDto {

    private List<ResponseCommentDto> comments;
    private Boolean isMoreComments;
}
