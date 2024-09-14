package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.exception.story.StoryNotFoundException;
import com.pigeon_stargram.sns_clone.repository.story.StoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.STORY_NOT_FOUND_ID;

// 스토리 정보에 대한 캐싱을 적용한 StoryCrudService 구현체
// Value  | Structure | Key
// -----  | --------- | ----------------------------------------
// story  | String    | STORY_ID_{storyId}
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StoryCrudServiceV2 implements StoryCrudService {

    private final StoryRepository storyRepository;

    @CachePut(value = STORY, key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).STORY_ID + '_' + #story.id", unless = "#result == null")
    public Story save(Story story) {
        return storyRepository.save(story);
    }

    @Cacheable(value = STORY, key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).STORY_ID + '_' + #storyId", unless = "#result == null")
    public Story findById(Long storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(STORY_NOT_FOUND_ID));
    }

    @CacheEvict(value = STORY, key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).STORY_ID + '_' + #storyId")
    public void delete(Long storyId) {
        storyRepository.deleteById(storyId);
    }
}
