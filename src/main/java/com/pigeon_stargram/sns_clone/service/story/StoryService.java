package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoriesDto;
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

    public ResponseStoriesDto getRecentStories(Long userId, Long currentMemberId) {
        User user = userService.findById(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.minusHours(24);

        List<Story> recentStories = storyRepository.findAllByUserAndCreatedDateAfter(user, expirationTime);
        List<ResponseStoryDto> storyDtos = recentStories.stream()
                .map(ResponseStoryDto::new)
                .toList();

        Integer lastReadIndex = getLastReadIndex(currentMemberId, recentStories);

        return new ResponseStoriesDto(storyDtos, lastReadIndex);
    }

    private Integer getLastReadIndex(Long currentMemberId, List<Story> recentStories) {
        int lastReadIndex = 0;
        for (int i = 0; i < recentStories.size(); i++) {
            if (!hasUserViewedStory(recentStories.get(i).getId(), currentMemberId)) {
                lastReadIndex = i;
                break;
            }
        }

        if (lastReadIndex == recentStories.size()) {
            lastReadIndex = 0;
        }
        return lastReadIndex;
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

    public Boolean hasRecentStory(Long userId) {
        User user = userService.findById(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.minusHours(24);

        return storyRepository.existsByUserAndCreatedDateAfter(user, expirationTime);
    }

    public boolean hasUnreadStories(Long userId, Long currentMemberId) {
        List<Story> stories = storyRepository.findAllByUserAndCreatedDateAfter(
                userService.findById(userId), LocalDateTime.now().minusHours(24)
        );

        return stories.stream().anyMatch(story -> !hasUserViewedStory(story.getId(), currentMemberId));
    }


}
