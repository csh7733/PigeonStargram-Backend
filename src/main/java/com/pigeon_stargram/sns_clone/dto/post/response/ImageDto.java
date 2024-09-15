package com.pigeon_stargram.sns_clone.dto.post.response;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import lombok.*;

/**
 * ImageDto는 게시물에 포함된 이미지 정보를 표현하는 DTO(Data Transfer Object)입니다.
 * 이 DTO는 게시물의 이미지와 해당 이미지의 특성을 클라이언트에 전달하기 위해 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ImageDto {

    private String img;         // 이미지의 URL
    private Boolean featured;   // 해당 이미지가 게시물에서 대표 이미지인지 여부

}
