package com.pigeon_stargram.sns_clone.service.story;

import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;

public class StoryBuilder {

    public static UploadStoryDto buildUploadStoryDto(Long userId, String content, String imageUrl) {
        return UploadStoryDto.builder()
                .userId(userId)
                .content(content)
                .imageUrl(imageUrl)
                .build();
    }

    public static MarkStoryAsViewedDto buildMarkStoryAsViewedDto(Long storyId, Long userId) {
        return MarkStoryAsViewedDto.builder()
                .storyId(storyId)
                .userId(userId)
                .build();
    }

    public static GetRecentStoriesDto buildGetRecentStoriesDto(Long userId, Long currentMemberId) {
        return GetRecentStoriesDto.builder()
                .userId(userId)
                .currentMemberId(currentMemberId)
                .build();
    }

    public static Story buildStory(User user, String content, String img) {
        return Story.builder()
                .user(user)
                .content(content)
                .img(img)
                .build();
    }
}
