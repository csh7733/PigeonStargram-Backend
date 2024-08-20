package com.pigeon_stargram.sns_clone.controller.story;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.story.Story;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.story.request.RequestAddStoryDto;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoryDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.story.StoryService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/stories")
@RestController
public class StoryController {

    private final StoryService storyService;
    private final FileService fileService;

    @PostMapping
    public void uploadStory(@LoginUser SessionUser loginUser,
                            @ModelAttribute RequestAddStoryDto request,
                            @RequestPart(value = "images") MultipartFile imageFile) {
        Long userId = loginUser.getId();
        String content = request.getContent();

        String imageUrl = fileService.saveFile(imageFile);

        storyService.uploadStory(userId, content, imageUrl);
    }

    @DeleteMapping("/{storyId}")
    public void deleteStory(@LoginUser SessionUser loginUser,
                            @PathVariable Long storyId) {

        storyService.deleteStory(storyId);
    }

    @GetMapping("/recent/{userId}")
    public List<ResponseStoryDto> getRecentStories(@PathVariable Long userId) {

        return storyService.getRecentStories(userId);
    }

    @PostMapping("/{storyId}/view")
    public void markStoryAsViewed(@LoginUser SessionUser loginUser,
                                  @PathVariable Long storyId) {
        Long userId = loginUser.getId();

        storyService.markStoryAsViewed(storyId, userId);
    }

    @GetMapping("/{storyId}/viewers")
    public List<ResponseUserInfoDto> getUserInfosWhoViewedStory(@PathVariable Long storyId) {

        return storyService.getUserInfosWhoViewedStory(storyId);
    }

}
