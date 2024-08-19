package com.pigeon_stargram.sns_clone.dto.search.response;

import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostsContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDataDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsLikeDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsProfileDto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTopSearchDto {
    private String term;

    public ResponseTopSearchDto(SearchTerm searchTerm){
        this.term = searchTerm.getTerm();
    }
}
