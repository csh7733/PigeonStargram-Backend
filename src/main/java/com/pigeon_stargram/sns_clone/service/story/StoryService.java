package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoryDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.exception.story.StoryNotFoundException;
import com.pigeon_stargram.sns_clone.repository.story.StoryRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserService userService;
    private final Map<Long, Set<Long>> storyViews = new ConcurrentHashMap<>();

    public Story uploadStory(Long userId, String content, String img) {
        User user = userService.findById(userId);

        Story story = Story.builder()
                .user(user)
                .content(content)
                .img(img)
                .build();

        return storyRepository.save(story);
    }

    public void deleteStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("Story not found"));

        storyRepository.delete(story);
    }

    public List<ResponseStoryDto> getRecentStories(Long userId) {
        User user = userService.findById(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.minusHours(24);

        return storyRepository.findAllByUserAndCreatedDateAfter(user, expirationTime).stream()
                .map(ResponseStoryDto::new)
                .toList();
    }

    public void markStoryAsViewed(Long storyId, Long userId) {

        storyViews.computeIfAbsent(storyId, k -> ConcurrentHashMap.newKeySet()).add(userId);
    }

    public List<ResponseUserInfoDto> getUserInfosWhoViewedStory(Long storyId) {
        Set<Long> userIdsWhoViewed = storyViews.getOrDefault(storyId, Collections.emptySet());

        return userService.getUserInfosByUserIds(new ArrayList<>(userIdsWhoViewed));
    }


    public boolean hasUserViewedStory(Long storyId, Long userId) {

        return storyViews.getOrDefault(storyId, Collections.emptySet()).contains(userId);
    }

}
