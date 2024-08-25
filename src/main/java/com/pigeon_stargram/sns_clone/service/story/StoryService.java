package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;
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
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.STORY_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.service.story.StoryBuilder.buildStory;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getExpirationTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserService userService;

    //레디스 사용전 임시로 메모리에서 관리할 set Map<StoryId, Set<UserID>>
    private final Map<Long, Set<Long>> storyViews = new ConcurrentHashMap<>();

    public Story uploadStory(UploadStoryDto dto) {
        User user = userService.findById(dto.getUserId());
        Story story = buildStory(user, dto.getContent(), dto.getImageUrl());

        return storyRepository.save(story);
    }

    public void deleteStory(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(STORY_NOT_FOUND_ID));

        storyRepository.delete(story);
    }

    public void markStoryAsViewed(MarkStoryAsViewedDto dto) {
        storyViews.computeIfAbsent(dto.getStoryId(), k -> ConcurrentHashMap.newKeySet()).add(dto.getUserId());
    }

    public ResponseStoriesDto getRecentStories(GetRecentStoriesDto dto) {
        User user = userService.findById(dto.getUserId());

        List<Story> recentStories = findRecentStories(user);

        List<ResponseStoryDto> storyDtos = recentStories.stream()
                .map(ResponseStoryDto::new)
                .collect(Collectors.toList());

        Integer lastReadIndex = getLastReadIndex(dto.getCurrentMemberId(), recentStories);

        return new ResponseStoriesDto(storyDtos, lastReadIndex);
    }

    private Integer getLastReadIndex(Long currentMemberId, List<Story> recentStories) {
        for (int i = 0; i < recentStories.size(); i++) {
            if (!hasUserViewedStory(recentStories.get(i).getId(), currentMemberId)) {
                return i;
            }
        }
        return 0;
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

        return storyRepository.existsByUserAndCreatedDateAfter(user, getExpirationTime());
    }

    public boolean hasUnreadStories(Long userId, Long currentMemberId) {
        User user = userService.findById(userId);
        List<Story> stories = findRecentStories(user);

        return stories.stream().anyMatch(story -> !hasUserViewedStory(story.getId(), currentMemberId));
    }

    private List<Story> findRecentStories(User user) {
        return storyRepository.findAllByUserAndCreatedDateAfter(user, getExpirationTime());
    }


}
