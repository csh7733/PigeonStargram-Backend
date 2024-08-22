package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePostDto {
    private Long id;
    private PostProfileDto profile;
    private PostDataDto data;
}
