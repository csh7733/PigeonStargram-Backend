package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시물 CRUD (Create, Read, Update, Delete) 작업을 수행하는 서비스 인터페이스입니다.
 */
public interface PostCrudService {

    /**
     * 게시물 ID를 통해 게시물을 조회합니다.
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    Post findById(Long postId);

    /**
     * 사용자 ID로 게시물 ID 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 게시물 ID 리스트
     */
    List<Long> findPostIdByUserId(Long userId);

    /**
     * 사용자 ID와 작성 날짜를 기준으로 게시물 ID 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param createdDate 게시물 작성 날짜
     * @return 최근 게시물 ID 리스트
     */
    List<Long> findPostIdsByUserIdAndCreatedDateAfter(Long userId, LocalDateTime createdDate);

    /**
     * 게시물을 데이터베이스에 저장합니다.
     * @param post 저장할 게시물
     * @return 저장된 게시물
     */
    Post save(Post post);

    /**
     * 게시물의 내용을 수정합니다.
     * @param postId 수정할 게시물의 ID
     * @param newContent 새로운 게시물 내용
     * @return 수정된 게시물
     */
    Post edit(Long postId, String newContent);

    /**
     * 게시물의 이미지를 수정합니다.
     * @param post 수정할 게시물
     * @return 수정된 게시물
     */
    Post updateImage(Post post);

    /**
     * 게시물 ID를 통해 게시물을 삭제합니다.
     * @param postId 삭제할 게시물의 ID
     */
    void deleteById(Long postId);
}
