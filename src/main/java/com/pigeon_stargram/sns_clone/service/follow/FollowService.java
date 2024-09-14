package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;

import java.util.List;

public interface FollowService {

    /**
     * 주어진 사용자 ID를 기준으로 팔로워 목록을 조회합니다.
     *
     * @param dto 팔로워 조회를 위한 DTO
     * @return 팔로워 목록
     */
    List<ResponseFollowerDto> findFollowers(FindFollowersDto dto);

    /**
     * 주어진 사용자 ID를 기준으로 팔로잉 목록을 조회합니다.
     *
     * @param dto 팔로잉 조회를 위한 DTO
     * @return 팔로잉 목록
     */
    List<ResponseFollowerDto> findFollowings(FindFollowingsDto dto);

    /**
     * 사용자 ID로 팔로잉 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 팔로잉 목록
     */
    List<ResponseFollowerDto> findFollowings(Long userId);

    /**
     * 사용자 ID로 팔로워 ID 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 팔로워 ID 목록
     */
    List<Long> getFollowerIds(Long userId);

    /**
     * 알림이 활성화된 팔로워 ID 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 알림이 활성화된 팔로워 ID 목록
     */
    List<Long> findNotificationEnabledFollowerIds(Long userId);

    /**
     * 주어진 대상 사용자에 대해 알림 설정 여부를 확인합니다.
     *
     * @param dto 알림 설정 조회를 위한 DTO
     * @return 알림 설정 여부 (true: 설정됨, false: 설정되지 않음)
     */
    Boolean getNotificationEnabled(GetNotificationEnabledDto dto);

    /**
     * 사용자 ID로 팔로잉 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 팔로잉 수
     */
    Long countFollowings(Long userId);

    /**
     * 사용자 ID로 팔로워 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 팔로워 수
     */
    Long countFollowers(Long userId);

    /**
     * 주어진 대상이 팔로잉 상태인지 확인합니다.
     *
     * @param sourceId 팔로우 요청자 ID
     * @param targetId 팔로우 대상자 ID
     * @return 팔로우 여부 (true: 팔로우 중, false: 팔로우 중 아님)
     */
    Boolean isFollowing(Long sourceId, Long targetId);

    /**
     * 주어진 사용자가 유명 사용자(famous user)인지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 유명 사용자 여부 (true: 유명 사용자, false: 일반 사용자)
     */
    Boolean isFamousUser(Long userId);

    /**
     * 팔로우 요청을 처리하고 알림을 전송합니다.
     *
     * @param dto 팔로우 요청 DTO
     */
    void createFollow(AddFollowDto dto);

    /**
     * 팔로워 알림 설정을 토글합니다 (켜기/끄기).
     *
     * @param dto 알림 설정 토글을 위한 DTO
     */
    void toggleNotificationEnabled(ToggleNotificationEnabledDto dto);

    /**
     * 팔로우 관계를 삭제합니다.
     *
     * @param dto 팔로우 삭제를 위한 DTO
     */
    void deleteFollow(DeleteFollowDto dto);

    /**
     * 채팅을 할 수 있는 팔로우 대상 목록을 조회합니다.
     *
     * @param loginUserId 현재 로그인된 사용자 ID
     * @return 채팅 가능한 대상 목록
     */
    List<ResponseUserChatDto> findPartnersForChat(Long loginUserId);

    /**
     * 사용자의 스토리와 팔로잉의 최근 스토리가 있는 사람들을 함께 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 최근 스토리가 있는 팔로잉 목록 및 사용자 정보
     */
    List<ResponseFollowerDto> findMeAndFollowingsWithRecentStories(Long userId);

    /**
     * 주어진 사용자가 팔로우하는 사람들 중에서 대상 사용자를 팔로우하는 사용자 목록을 조회합니다.
     *
     * @param sourceId 팔로우하는 사람의 ID
     * @param targetId 대상 사용자의 ID
     * @return 대상 사용자를 팔로우하는 팔로잉 목록
     */
    List<Long> findFollowingsWhoFollowTarget(Long sourceId, Long targetId);
}
