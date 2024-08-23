package com.pigeon_stargram.sns_clone.dto.search.response;

import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTopSearchDto {
    private String term;
}
