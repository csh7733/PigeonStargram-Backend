package com.pigeon_stargram.sns_clone.dto.reply.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class newReplyDto {
    private String content;
    private List<Long> taggedUserIds;
}