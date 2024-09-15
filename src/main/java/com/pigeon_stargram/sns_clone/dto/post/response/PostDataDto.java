package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
/**
 * PostDataDto는 게시물(Post)와 관련된 다양한 정보를 클라이언트에게 전달하기 위한 데이터 전송 객체입니다.
 * 게시물의 내용, 이미지, 좋아요, 댓글 등의 정보를 포함합니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostDataDto {

    private String content;                     // 게시물의 내용
    private List<ImageDto> images;              // 게시물에 포함된 이미지 리스트
    private PostLikeDto likes;                  // 게시물의 좋아요 정보
    private List<ResponseCommentDto> comments;  // 게시물에 대한 댓글 리스트
    private Boolean isMoreComments;             // 댓글이 더 있는지 여부를 나타내는 플래그
}