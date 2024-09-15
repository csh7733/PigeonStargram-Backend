package com.pigeon_stargram.sns_clone.dto.post.internal;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.post.response.ImageDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostProfileDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
/**
 * PostContentDto는 게시물의 내용을 표현하는 DTO(Data Transfer Object)입니다.
 * 이 DTO는 게시물의 기본 정보와 관련된 데이터를 클라이언트에 전달하기 위해 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostContentDto {

    private Long id;
    private PostProfileDto profile; // 게시물 작성자의 프로필 정보

    // PostsDataDto의 필드중 일부
    private String content;         // 게시물의 텍스트 내용
    private List<ImageDto> images;  // 게시물에 포함된 이미지 목록
}
