package com.pigeon_stargram.sns_clone.dto.user.request;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestCurrentMemberDto {
    private String name;
}
