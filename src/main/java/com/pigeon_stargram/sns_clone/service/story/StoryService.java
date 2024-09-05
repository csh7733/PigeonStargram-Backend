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
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.STORY_NOT_FOUND_ID;
import static com.pigeon_stargram.sns_clone.service.story.StoryBuilder.buildStory;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getExpirationTime;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StoryService {

    private final StoryCrudService storyCrudService;
    private final UserService userService;
    private final RedisService redisService;

    public Story uploadStory(UploadStoryDto dto) {
        User user = userService.findById(dto.getUserId());
        Story story = buildStory(user, dto.getContent(), dto.getImageUrl());

        // 캐시와 동기화된 저장
        Story savedStory = storyCrudService.save(story);

        // 캐시에 storyId 추가
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, dto.getUserId().toString());
        redisService.addToSet(userStorySetKey, savedStory.getId());

        return savedStory;
    }


    public void deleteStory(Long storyId) {
        // 캐시나 데이터베이스에서 스토리 조회
        Story story = storyCrudService.findById(storyId);

        // 사용자 스토리 Set에서 storyId 제거
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, story.getUser().getId().toString());
        redisService.removeFromSet(userStorySetKey, storyId);

        // 캐시와 데이터베이스에서 삭제
        storyCrudService.delete(storyId);
    }

    public void markStoryAsViewed(MarkStoryAsViewedDto dto) {
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, dto.getStoryId().toString());
        redisService.addToSet(redisSetKey, dto.getUserId());
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
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, storyId.toString());
        List<Long> userIdsWhoViewed = redisService.getSetAsLongList(redisSetKey);

        return userIdsWhoViewed.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), userIdList ->
                        userIdList.isEmpty() ? Collections.emptyList() : userService.getUserInfosByUserIds(userIdList)
                ));
    }

    public boolean hasUserViewedStory(Long storyId, Long userId) {
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, storyId.toString());

        return redisService.isMemberOfSet(redisSetKey, userId);
    }

    public Boolean hasRecentStory(Long userId) {
        User user = userService.findById(userId);

        // 유효한 스토리 ID를 가져옴
        List<Long> recentStoryIds = findRecentStoryIds(user);

        // 유효한 스토리 ID가 하나라도 있으면 true 반환
        return !recentStoryIds.isEmpty();
    }

    public boolean hasUnreadStories(Long userId, Long currentMemberId) {
        User user = userService.findById(userId);

        // 유효한 스토리 ID를 가져옴
        List<Long> recentStoryIds = findRecentStoryIds(user);

        // 유효한 스토리 ID 중에서 사용자가 읽지 않은 스토리가 있는지 확인
        return recentStoryIds.stream()
                .anyMatch(storyId -> !hasUserViewedStory(storyId, currentMemberId));
    }


    private List<Story> findRecentStories(User user) {
        List<Long> recentStoryIds = findRecentStoryIds(user);

        return recentStoryIds.stream()
                .map(storyCrudService::findById)
                .collect(Collectors.toList());
    }

    private List<Long> findRecentStoryIds(User user) {
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, user.getId().toString());

        // 만료된 스토리들을 제거
        removeExpiredStoriesFromSet(userStorySetKey);

        // 유효한 storyId들을 반환
        return redisService.getSetAsLongList(userStorySetKey);
    }

    private void removeExpiredStoriesFromSet(String userStorySetKey) {
        List<Long> storyIds = redisService.getSetAsLongList(userStorySetKey);

        for (Long storyId : storyIds) {
            Story story = storyCrudService.findById(storyId);

            // 24시간이 지난 스토리인지 확인
            if (story.getCreatedDate().isBefore(getExpirationTime())) {
                redisService.removeFromSet(userStorySetKey, storyId);
            }
        }
    }


}
