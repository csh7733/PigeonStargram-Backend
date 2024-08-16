package com.pigeon_stargram.sns_clone.dto.post.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreatePostDto {

    private String content;
    private List<Long> taggedUserIds;
}
