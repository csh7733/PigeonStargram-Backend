package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostLikeDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;

import java.util.List;

/**
 * 게시물 관련 서비스의 인터페이스입니다.
 * 게시물의 조회, 생성, 수정, 삭제, 좋아요 처리 및 알림 전송을 담당합니다.
 */
public interface PostService {

    /**
     * 게시물 ID로 게시물을 조회합니다.
     *
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    Post findById(Long postId);

    /**
     * 사용자 ID로 게시물 리스트를 조회합니다.
     * 업로드 중인 게시물은 제외합니다.
     *
     * @param userId 사용자 ID
     * @return 게시물 DTO 리스트
     */
    List<ResponsePostDto> getPostsByUserId(Long userId);

    /**
     * 사용자 ID로 최근 게시물을 조회합니다.
     * 최근 24시간 내 작성된 게시물만 포함됩니다.
     *
     * @param userId 사용자 ID
     * @return 최근 게시물 DTO 리스트
     */
    List<ResponsePostDto> getRecentPostsByUser(Long userId);

    /**
     * 게시물 ID로 게시물의 구성 요소를 조합하여 반환합니다.
     * 게시물 내용, 좋아요 수, 댓글 및 댓글 추가 여부를 포함합니다.
     *
     * @param postId 게시물 ID
     * @return 게시물 DTO
     */
    ResponsePostDto getCombinedPost(Long postId);

    /**
     * 게시물 ID로 게시물을 조회합니다.
     * 업로드 중인 게시물은 반환하지 않습니다.
     *
     * @param postId 게시물 ID
     * @return 게시물 DTO
     */
    ResponsePostDto getPostByPostId(Long postId);

    /**
     * 게시물 ID로 게시물 내용을 조회합니다.
     * 캐시된 이미지가 있는 경우 이를 복사하여 게시물 내용에 포함시킵니다.
     *
     * @param postId 게시물 ID
     * @return 게시물 내용 DTO
     */
    PostContentDto getPostContent(Long postId);

    /**
     * 게시물 ID로 게시물의 좋아요 수를 조회합니다.
     *
     * @param postId 게시물 ID
     * @return 게시물 좋아요 DTO
     */
    PostLikeDto getPostsLike(Long postId);

    /**
     * 게시물을 생성합니다.
     * 게시물에 포함된 이미지와 알림, 타임라인 처리를 수행합니다.
     *
     * @param dto 게시물 생성 DTO
     * @return 생성된 게시물 ID
     */
    Long createPost(CreatePostDto dto);

    /**
     * 게시물을 수정합니다.
     *
     * @param dto 게시물 수정 DTO
     */
    void editPost(EditPostDto dto);

    /**
     * 게시물을 삭제합니다.
     * 게시물에 대한 모든 댓글도 함께 삭제합니다.
     *
     * @param postId 게시물 ID
     */
    void deletePost(Long postId);

    /**
     * 게시물에 좋아요를 추가하거나 제거합니다.
     * 좋아요 수가 증가할 때 알림을 보내고, true를 반환합니다.
     *
     * @param dto 게시물 좋아요 DTO
     * @return 좋아요 처리 여부
     */
    Boolean likePost(LikePostDto dto);
}