package com.pigeon_stargram.sns_clone.service.story.implV2;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoryDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.story.StoryCrudService;
import com.pigeon_stargram.sns_clone.service.story.StoryService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.domain.story.StoryFactory.createStory;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getExpirationTime;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

// 스토리 정보에 대한 캐싱을 적용한 StoryServiceV2 구현체
// Value    | Structure | Key
// -------- | --------- | ----------------------------------------
// storyId  | Set       | USER_STORIES_USER_ID_{userId}            (사용자별 스토리 목록)
// userId   | Set       | STORY_VIEWS_STORY_ID_{storyId}           (스토리별 조회한 사용자 목록)
@Service
@RequiredArgsConstructor
@Slf4j
public class StoryServiceV2 implements StoryService {

    private final StoryCrudService storyCrudService;
    private final UserService userService;
    private final RedisService redisService;

    @Transactional
    public Story uploadStory(UploadStoryDto dto) {
        User user = userService.getUserById(dto.getUserId());
        Story story = createStory(user, dto.getContent(), dto.getImageUrl());

        // 스토리를 데이터베이스에 저장 (write-through 방식으로 캐시와 동기화)
        Story savedStory = storyCrudService.save(story);

        // 사용자 스토리 목록에 해당 스토리 ID를 캐시에 추가
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, dto.getUserId().toString());
        redisService.addToSet(userStorySetKey, savedStory.getId());

        return savedStory;
    }

    @Transactional
    public void deleteStory(Long storyId) {
        // 캐시 또는 데이터베이스에서 스토리를 조회
        Story story = storyCrudService.findById(storyId);

        // 사용자 스토리 목록에서 해당 스토리 ID를 캐시에서 제거
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, story.getUser().getId().toString());
        redisService.removeFromSet(userStorySetKey, storyId);

        // 데이터베이스에서 삭제하고, 캐시에서도 해당 스토리 ID 제거
        storyCrudService.delete(storyId);
    }

    public void markStoryAsViewed(MarkStoryAsViewedDto dto) {
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, dto.getStoryId().toString());

        // 스토리를 조회한 사용자 ID를 캐시에 추가
        redisService.addToSet(redisSetKey, dto.getUserId());
    }

    @Transactional(readOnly = true)
    public ResponseStoriesDto getRecentStories(GetRecentStoriesDto dto) {
        // 조회할 사용자를 가져옴
        User user = userService.getUserById(dto.getUserId());

        // 사용자의 최근 스토리 목록을 조회
        List<Story> recentStories = findRecentStories(user);

        // 스토리 엔티티를 응답 DTO로 변환
        List<ResponseStoryDto> storyDtos = recentStories.stream()
                .map(ResponseStoryDto::new)
                .collect(Collectors.toList());

        // 현재 사용자가 마지막으로 읽은 스토리의 인덱스를 계산
        Integer lastReadIndex = getLastReadIndex(dto.getCurrentMemberId(), recentStories);

        // 최근 스토리 목록과 마지막 읽은 인덱스를 반환
        return new ResponseStoriesDto(storyDtos, lastReadIndex);
    }

    @Transactional(readOnly = true)
    public List<ResponseUserInfoDto> getUserInfosWhoViewedStory(Long storyId) {
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, storyId.toString());

        // 해당 스토리를 본 사용자들의 ID를 캐시에서 조회
        List<Long> userIdsWhoViewed = redisService.getSetAsLongList(redisSetKey);

        // 조회한 사용자들의 ID 리스트가 비어있으면 빈 리스트 반환, 그렇지 않으면 사용자 정보를 반환
        return userIdsWhoViewed.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), userIdList ->
                        userIdList.isEmpty() ? Collections.emptyList() : userService.getUserInfosByUserIds(userIdList)
                ));
    }

    public Boolean hasUserViewedStory(Long storyId, Long userId) {
        String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, storyId.toString());

        // 사용자가 해당 스토리를 조회했는지 여부를 반환
        return redisService.isMemberOfSet(redisSetKey, userId);
    }

    @Transactional(readOnly = true)
    public Boolean hasRecentStory(Long userId) {
        User user = userService.getUserById(userId);

        // 사용자의 유효한(24시간 이내) 스토리 ID 목록을 가져옴
        List<Long> recentStoryIds = findRecentStoryIds(user);

        // 유효한 스토리 ID가 존재하면 true 반환, 없으면 false 반환
        return !recentStoryIds.isEmpty();
    }

    @Transactional(readOnly = true)
    public Boolean hasUnreadStories(Long userId, Long currentMemberId) {
        User user = userService.getUserById(userId);

        // 사용자의 유효한(24시간 이내) 스토리 ID 목록을 가져옴
        List<Long> recentStoryIds = findRecentStoryIds(user);

        // 유효한 스토리 중에서 현재 사용자가 읽지 않은 스토리가 있는지 확인
        return recentStoryIds.stream()
                .anyMatch(storyId -> !hasUserViewedStory(storyId, currentMemberId));
    }

    private Integer getLastReadIndex(Long currentMemberId, List<Story> recentStories) {
        // 최근 스토리 목록에서 사용자가 읽지 않은 첫 번째 스토리의 인덱스를 찾음
        for (int i = 0; i < recentStories.size(); i++) {
            if (!hasUserViewedStory(recentStories.get(i).getId(), currentMemberId)) {
                return i; // 사용자가 읽지 않은 스토리의 인덱스 반환
            }
        }
        // 모든 스토리를 읽은 경우 0 반환
        return 0;
    }

    private List<Story> findRecentStories(User user) {
        // 사용자의 최근 스토리 ID 목록을 가져옴
        List<Long> recentStoryIds = findRecentStoryIds(user);

        // 각 스토리 ID를 사용하여 스토리 엔티티를 조회한 후 리스트로 반환
        return recentStoryIds.stream()
                .map(storyCrudService::findById)
                .collect(Collectors.toList());
    }

    private List<Long> findRecentStoryIds(User user) {
        String userStorySetKey = cacheKeyGenerator(USER_STORIES, USER_ID, user.getId().toString());

        // 만료된(24시간이 지난) 스토리들을 캐시에서 제거
        removeExpiredStoriesFromSet(userStorySetKey);

        // 유효한 스토리 ID 목록을 가져와 반환
        return redisService.getSetAsLongList(userStorySetKey);
    }

    private void removeExpiredStoriesFromSet(String userStorySetKey) {
        // 사용자 스토리 목록에서 모든 스토리 ID를 가져옴
        List<Long> storyIds = redisService.getSetAsLongList(userStorySetKey);

        for (Long storyId : storyIds) {
            Story story = storyCrudService.findById(storyId);

            // 스토리가 24시간이 지났는지 확인
            if (story.getCreatedDate().isBefore(getExpirationTime())) {
                // 만료된 스토리를 사용자 스토리 목록에서 제거
                redisService.removeFromSet(userStorySetKey, storyId);

                String redisSetKey = cacheKeyGenerator(STORY_VIEWS, STORY_ID, storyId.toString());

                // 조회수 데이터를 저장한 Redis 키 삭제
                redisService.removeSet(redisSetKey);
            }
        }
    }

}
