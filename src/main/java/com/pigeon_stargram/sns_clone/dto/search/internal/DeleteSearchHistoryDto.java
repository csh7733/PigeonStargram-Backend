package com.pigeon_stargram.sns_clone.dto.search.internal;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSearchHistoryDto {

    private Long userId;
    private String query;
}
