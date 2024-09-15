package com.pigeon_stargram.sns_clone.dto.post.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostLikeDto {

    private Boolean like;   // 사용하지 않는 필드
    private Integer value;  // 해당 게시물의 총 좋아요 수

}