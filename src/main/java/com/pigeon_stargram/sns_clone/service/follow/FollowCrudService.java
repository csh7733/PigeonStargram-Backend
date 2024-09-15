package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;

import java.util.List;


/**
 * 팔로우(Follow) 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 팔로워 및 팔로잉 목록 조회, 팔로우 저장/삭제, 알림 설정 관리 등의 기능을 제공합니다.
 */
public interface FollowCrudService {

    /**
     * 주어진 사용자 ID를 기준으로 팔로워 ID 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 팔로워 ID 목록
     */
    List<Long> findFollowerIds(Long userId);

    /**
     * 주어진 사용자 ID를 기준으로 팔로잉 ID 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 팔로잉 ID 목록
     */
    List<Long> findFollowingIds(Long userId);

    /**
     * 주어진 사용자 ID를 기준으로 알림이 활성화된 사용자 ID 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 알림이 활성화된 사용자 ID 목록
     */
    List<Long> findNotificationEnabledIds(Long userId);

    /**
     * 팔로우 관계를 저장하고 관련된 캐시를 업데이트합니다.
     *
     * @param follow 저장할 팔로우 객체
     */
    void save(Follow follow);

    /**
     * 특정 사용자의 알림 설정을 토글합니다.
     *
     * @param senderId   알림 설정을 변경할 사용자 ID
     * @param recipientId 알림을 받을 대상 사용자 ID
     */
    void toggleNotificationEnabled(Long senderId, Long recipientId);

    /**
     * 특정 팔로우 관계를 삭제하고 관련 캐시를 업데이트합니다.
     *
     * @param senderId   팔로우를 삭제할 사용자 ID
     * @param recipientId 팔로우 대상 사용자 ID
     */
    void deleteFollowBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
