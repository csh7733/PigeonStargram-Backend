package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.post.PostServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.post.PostDtoConverter.*;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

/**
 * PostController는 게시물(Post)와 관련된 HTTP 요청을 처리하는 컨트롤러입니다.
 * 게시물 조회, 생성, 수정, 삭제, 좋아요 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final FileService fileService;

    /**
     * 특정 사용자의 게시물 목록을 조회하는 메서드.
     * @param userId 조회할 사용자의 ID
     * @return ResponsePostDto 리스트로 반환된 게시물 목록
     */
    @GetMapping
    public List<ResponsePostDto> getPosts(@RequestParam Long userId) {
        logControllerMethod("getPosts", userId);

        return postService.getPostsByUserId(userId);
    }

    /**
     * 새로운 게시물을 생성하는 메서드.
     * 파일 업로드(비동기) -> 게시물 저장 ->
     * @param loginUser 로그인한 사용자의 세션 정보
     * @param request 게시물 생성 요청 정보
     * @param imagesFile 첨부된 이미지 파일 목록
     * @return 생성된 게시물의 ID
     */
    @PostMapping
    public Long createPosts(@LoginUser SessionUser loginUser,
                            @ModelAttribute RequestCreatePostDto request,
                            @RequestPart(value = "images", required = false)
                            List<MultipartFile> imagesFile) {
        logControllerMethod("createPosts", loginUser, request, imagesFile);

        // 이미지 파일 저장 처리
        FileUploadResultDto savedFileDto = fileService.saveFiles(imagesFile);
        List<String> images = savedFileDto.getFileNames();
        String fieldKey = savedFileDto.getFieldKey();

        CreatePostDto dto = toCreatePostDto(request, loginUser, images, fieldKey);
        return postService.createPost(dto);
    }

    /**
     * 특정 게시물의 세부 정보를 조회하는 메서드.
     * @param loginUser 로그인한 사용자의 세션 정보
     * @param postId 조회할 게시물의 ID
     * @return ResponsePostDto 게시물 상세 정보
     */
    @GetMapping("/{postId}")
    public ResponsePostDto getPost(@LoginUser SessionUser loginUser,
                                   @PathVariable Long postId) {
        logControllerMethod("getPost", loginUser, postId);

        return postService.getPostByPostId(postId);
    }

    /**
     * 게시물을 수정하는 메서드.
     * @param loginUser 로그인한 사용자의 세션 정보
     * @param postId 수정할 게시물의 ID
     * @param request 게시물 수정 요청 정보
     */
    @PatchMapping("/{postId}")
    public void editPost(@LoginUser SessionUser loginUser,
                         @PathVariable Long postId,
                         @RequestBody RequestEditPostDto request) {
        logControllerMethod("editPost", loginUser, postId, request);

        EditPostDto dto = toEditPostDto(request, postId);
        postService.editPost(dto);
    }

    /**
     * 게시물을 삭제하는 메서드.
     * @param loginUser 로그인한 사용자의 세션 정보
     * @param postId 삭제할 게시물의 ID
     */
    @DeleteMapping("/{postId}")
    public void deletePost(@LoginUser SessionUser loginUser,
                           @PathVariable Long postId) {
        logControllerMethod("deletePost", loginUser, postId);

        postService.deletePost(postId);
    }

    /**
     * 게시물에 좋아요를 추가하거나 제거하는 메서드.
     * @param loginUser 로그인한 사용자의 세션 정보
     * @param request 좋아요 요청 정보
     * @return Boolean true: 좋아요 생성, false: 좋아요 삭제
     */
    @PostMapping("/like")
    public Boolean likePost(@LoginUser SessionUser loginUser,
                            @RequestBody RequestLikePostDto request) {
        logControllerMethod("likePost", loginUser, request);

        LikePostDto dto = toLikePostDto(request, loginUser);
        return postService.likePost(dto);
    }
}
