package com.pigeon_stargram.sns_clone.dto.search.response;

import com.pigeon_stargram.sns_clone.domain.search.SearchHistory;
import lombok.*;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.formatTime;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSearchHistoryDto {
    private String searchQuery;
    private String time;

    public ResponseSearchHistoryDto(SearchHistory searchHistory){
        this.searchQuery = searchHistory.getSearchQuery();
        this.time = formatTime(searchHistory.getCreatedDate());
    }
}
