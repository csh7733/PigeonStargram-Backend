package com.pigeon_stargram.sns_clone.controller.story;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.story.internal.GetRecentStoriesDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.MarkStoryAsViewedDto;
import com.pigeon_stargram.sns_clone.dto.story.internal.UploadStoryDto;
import com.pigeon_stargram.sns_clone.dto.story.request.RequestAddStoryDto;
import com.pigeon_stargram.sns_clone.dto.story.response.ResponseStoriesDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.story.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.story.StoryDtoConvertor.*;

/**
 * 스토리와 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자가 스토리를 업로드, 삭제하거나, 스토리 조회와 관련된 작업을 수행할 수 있습니다.
 */
@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Slf4j
public class StoryController {

    private final StoryService storyService;
    private final FileService fileService;

    /**
     * 사용자가 새로운 스토리를 업로드합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param request   스토리 내용이 포함된 요청 DTO(String Content)
     * @param imageFile 업로드할 이미지 파일
     */
    @PostMapping
    public void uploadStory(@LoginUser SessionUser loginUser,
                            @ModelAttribute RequestAddStoryDto request,
                            @RequestPart(value = "image") MultipartFile imageFile) {
        Long userId = loginUser.getId();
        String content = request.getContent();

        String imageUrl = fileService.saveFile(imageFile);

        UploadStoryDto uploadStoryDto = toUploadStoryDto(userId, content, imageUrl);
        storyService.uploadStory(uploadStoryDto);
    }

    /**
     * 사용자가 특정 스토리를 삭제합니다.

     * @param storyId   삭제할 스토리의 ID
     */
    @DeleteMapping("/{storyId}")
    public void deleteStory(@PathVariable Long storyId) {

        storyService.deleteStory(storyId);
    }

    /**
     * 특정 사용자의 최근 스토리들을 조회합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param userId    조회할 사용자의 ID
     * @return 사용자의 최근 스토리 목록과 마지막 읽은 스토리의 인덱스를 포함한 DTO
     */
    @GetMapping("/recent/{userId}")
    public ResponseStoriesDto getRecentStories(@LoginUser SessionUser loginUser,
                                               @PathVariable Long userId) {
        Long currentMemberId = loginUser.getId();

        GetRecentStoriesDto getRecentStoriesDto = toGetRecentStoriesDto(userId, currentMemberId);
        return storyService.getRecentStories(getRecentStoriesDto);
    }

    /**
     * 사용자가 특정 스토리를 조회했음을 표시합니다.
     *
     * @param loginUser 현재 로그인한 사용자
     * @param storyId   조회한 스토리의 ID
     */
    @PostMapping("/{storyId}/view")
    public void markStoryAsViewed(@LoginUser SessionUser loginUser,
                                  @PathVariable Long storyId) {
        Long userId = loginUser.getId();

        MarkStoryAsViewedDto markStoryAsViewedDto = toMarkStoryAsViewedDto(storyId, userId);
        storyService.markStoryAsViewed(markStoryAsViewedDto);
    }

    /**
     * 특정 스토리를 본 사용자들의 정보를 조회합니다.
     *
     * @param storyId 조회할 스토리의 ID
     * @return 스토리를 본 사용자들의 정보 리스트 DTO
     */
    @GetMapping("/{storyId}/viewers")
    public List<ResponseUserInfoDto> getUserInfosWhoViewedStory(@PathVariable Long storyId) {

        return storyService.getUserInfosWhoViewedStory(storyId);
    }

}
