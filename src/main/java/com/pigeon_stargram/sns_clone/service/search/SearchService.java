package com.pigeon_stargram.sns_clone.service.search;

import com.pigeon_stargram.sns_clone.domain.search.SearchTerm;
import com.pigeon_stargram.sns_clone.dto.search.response.ResponseTopSearchDto;
import com.pigeon_stargram.sns_clone.repository.search.SearchTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchTermRepository searchTermRepository;

    public List<ResponseTopSearchDto> getTopSearchTermsByPrefix(String prefix) {
        return searchTermRepository.findTop5ByPrefixOrderByScoreDesc(prefix).stream()
                .map(ResponseTopSearchDto::new)
                .toList();
    }

}
