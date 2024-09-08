package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDataDto {
    private String content;
    private List<ImageDto> images;
    private PostLikeDto likes;
    private List<ResponseCommentDto> comments;
    private Boolean isMoreComments;
}