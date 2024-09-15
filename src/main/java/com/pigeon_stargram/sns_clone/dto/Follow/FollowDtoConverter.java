package com.pigeon_stargram.sns_clone.dto.Follow;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;

/**
 * DTO 변환을 담당하는 유틸리티 클래스입니다.
 *
 * 이 클래스는 다양한 DTO 객체를 생성하기 위한 정적 메서드를 제공합니다.
 * 예를 들어, 사용자 팔로우 요청, 알림 설정, 팔로우 및 팔로워 목록 조회 등의 DTO를 생성할 수 있습니다.
 */
public class FollowDtoConverter {

    /**
     * 로그인 사용자와 타겟 사용자를 기반으로 FindFollowingsDto 객체를 생성합니다.
     *
     * @param loginUserId 로그인 사용자 ID
     * @param userId 타겟 사용자 ID
     * @return FindFollowingsDto 객체
     */
    public static FindFollowingsDto toFindFollowingsDto(Long loginUserId,
                                                        Long userId) {
        return FindFollowingsDto.builder()
                .loginUserId(loginUserId)
                .userId(userId)
                .build();
    }

    /**
     * 로그인 사용자와 타겟 사용자를 기반으로 FindFollowersDto 객체를 생성합니다.
     *
     * @param loginUserId 로그인 사용자 ID
     * @param userId 타겟 사용자 ID
     * @return FindFollowersDto 객체
     */
    public static FindFollowersDto toFindFollowersDto(Long loginUserId,
                                                      Long userId) {
        return FindFollowersDto.builder()
                .loginUserId(loginUserId)
                .userId(userId)
                .build();
    }

    /**
     * 알림 설정을 조회하기 위한 GetNotificationEnabledDto 객체를 생성합니다.
     *
     * @param loginUserId 로그인 사용자 ID
     * @param targetUserId 타겟 사용자 ID
     * @return GetNotificationEnabledDto 객체
     */
    public static GetNotificationEnabledDto toGetNotificationEnabledDto(Long loginUserId,
                                                                        Long targetUserId) {
        return GetNotificationEnabledDto.builder()
                .loginUserId(loginUserId)
                .targetUserId(targetUserId)
                .build();
    }

    /**
     * 팔로우 추가 요청을 위한 AddFollowDto 객체를 생성합니다.
     *
     * @param senderId 발신자 사용자 ID
     * @param recipientId 수신자 사용자 ID
     * @return AddFollowDto 객체
     */
    public static AddFollowDto toAddFollowDto(Long senderId,
                                              Long recipientId) {
        return AddFollowDto.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
    }

    /**
     * 알림 설정을 토글하기 위한 ToggleNotificationEnabledDto 객체를 생성합니다.
     *
     * @param loginUserId 로그인 사용자 ID
     * @param targetUserId 타겟 사용자 ID
     * @return ToggleNotificationEnabledDto 객체
     */
    public static ToggleNotificationEnabledDto toToggleNotificationEnabledDto(Long loginUserId,
                                                                              Long targetUserId) {
        return ToggleNotificationEnabledDto.builder()
                .loginUserId(loginUserId)
                .targetUserId(targetUserId)
                .build();
    }

    /**
     * 팔로우 삭제 요청을 위한 DeleteFollowDto 객체를 생성합니다.
     *
     * @param senderId 발신자 사용자 ID
     * @param recipientId 수신자 사용자 ID
     * @return DeleteFollowDto 객체
     */
    public static DeleteFollowDto toDeleteFollowDto(Long senderId,
                                                    Long recipientId) {
        return DeleteFollowDto.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
    }

    /**
     * 사용자와 최근 스토리의 여부를 기반으로 ResponseFollowerDto 객체를 생성합니다.
     *
     * @param user 사용자 객체
     * @param hasUnreadStories 사용자에게 읽지 않은 스토리가 있는지 여부
     * @return ResponseFollowerDto 객체
     */
    public static ResponseFollowerDto toResponseFollowerDto(User user,
                                                            Boolean hasUnreadStories) {
        return ResponseFollowerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .location(user.getLocation())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .hasUnreadStories(hasUnreadStories)
                .build();
    }

    public static ResponseFollowerDto toResponseFollowerDto(User user,
                                                            Integer follow) {
        return ResponseFollowerDto.builder()
                .id(user.getId())
                .name(user.getName())
                .location(user.getLocation())
                .company(user.getCompany())
                .avatar(user.getAvatar())
                .follow(follow)
                .build();
    }
}