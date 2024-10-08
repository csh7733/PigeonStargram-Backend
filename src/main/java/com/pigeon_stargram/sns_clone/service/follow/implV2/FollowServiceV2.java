package com.pigeon_stargram.sns_clone.service.follow.implV2;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.follow.FollowFactory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.*;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.exception.follow.FollowExistException;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.story.StoryService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.constant.FollowConstants.*;
import static com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter.toResponseFollowerDto;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.toResponseUserChatDto;

/**
 * 팔로우 서비스 클래스입니다.
 *
 * 이 클래스는 캐시를 적용하여 팔로우 관련 비즈니스 로직을 처리하며,
 * 팔로워 및 팔로잉 목록 조회, 팔로우 요청 처리,
 * 알림 설정 및 기타 팔로우 관련 작업을 수행합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceV2 implements FollowService {

    private final FollowCrudService followCrudService;
    private final UserService userService;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final StoryService storyService;

    @Transactional(readOnly = true)
    @Override
    public List<ResponseFollowerDto> findFollowers(FindFollowersDto dto) {
        Set<Long> targetUserFollowerIdSet = findFollowerIdsAsSet(dto.getUserId());
        Set<Long> loginUserFollowingIdSet = findFollowingIdsAsSet(dto.getLoginUserId());

        // 타겟 유저를 팔로우하는 사람 중에서 로그인 사용자가 팔로우 중인 사람들
        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowerIdSet);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseFollowerDto> findFollowings(FindFollowingsDto dto) {
        Set<Long> targetUserFollowingIdSet = findFollowingIdsAsSet(dto.getUserId());
        Set<Long> loginUserFollowingIdSet = findFollowingIdsAsSet(dto.getLoginUserId());

        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowingIdSet);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseFollowerDto> findFollowings(Long userId) {
        return followCrudService.findFollowingIds(userId).stream()
                .map(userService::getUserById)
                .map(recipient -> toResponseFollowerDto(recipient, 1))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getFollowerIds(Long userId) {
        return followCrudService.findFollowerIds(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findNotificationEnabledFollowerIds(Long userId) {
        return followCrudService.findNotificationEnabledIds(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean getNotificationEnabled(GetNotificationEnabledDto dto) {
        return followCrudService.findNotificationEnabledIds(dto.getTargetUserId())
                .contains(dto.getLoginUserId());
    }

    @Transactional(readOnly = true)
    @Override
    public Long countFollowings(Long userId) {
        return (long) followCrudService.findFollowingIds(userId).size();
    }

    @Transactional(readOnly = true)
    @Override
    public Long countFollowers(Long userId) {
        return (long) followCrudService.findFollowerIds(userId).size();
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean isFollowing(Long sourceId, Long targetId) {
        return followCrudService.findFollowerIds(targetId)
                .contains(sourceId);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean isFamousUser(Long userId) {
        User user = userService.getUserById(userId);
        return user.getIsFamousUser();
    }

    @Transactional
    @Override
    public void createFollow(AddFollowDto dto) {
        Long senderId = dto.getSenderId();
        Long recipientId = dto.getRecipientId();

        // 이미 팔로우 중인 경우 예외 발생
        if (followCrudService.findFollowerIds(recipientId).contains(senderId)) {
            throw new FollowExistException("이미 팔로우 중입니다.");
        }

        User sender = userService.getUserById(senderId);
        User recipient = userService.getUserById(recipientId);

        Follow follow = FollowFactory.createFollow(sender, recipient);
        followCrudService.save(follow);

        // 만약 Recipient의 팔로워수가 임계점을 넘어간다면, 해당 유저를 앞으로 유명인으로 설정
        if(countFollowers(recipientId) >= FAMOUS_USER_THRESHOLD){
            userService.setFamousUser(recipientId);
        }

        dto.setSenderName(sender.getName());
        notificationService.sendToSplitWorker(dto);
    }

    @Transactional
    @Override
    public void toggleNotificationEnabled(ToggleNotificationEnabledDto dto) {
        followCrudService.toggleNotificationEnabled(dto.getLoginUserId(), dto.getTargetUserId());
    }

    @Transactional
    @Override
    public void deleteFollow(DeleteFollowDto dto) {
        followCrudService.deleteFollowBySenderIdAndRecipientId(dto.getSenderId(), dto.getRecipientId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseUserChatDto> findPartnersForChat(Long loginUserId) {
        List<Long> followerIds = followCrudService.findFollowerIds(loginUserId);
        List<Long> followingIds = followCrudService.findFollowingIds(loginUserId);

        return Stream.concat(followerIds.stream(), followingIds.stream())
                .distinct()
                .map(userId -> getResponseUserChatDto(loginUserId, userId, followingIds, followerIds))
                .sorted(Comparator.comparing(
                        ResponseUserChatDto::getLastMessage,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseFollowerDto> findMeAndFollowingsWithRecentStories(Long userId) {
        List<ResponseFollowerDto> followingsWithRecentStories = getFollowingsWithRecentStories(userId);

        // 현재 사용자가 최근 스토리가 있는지 확인
        boolean currentUserHasRecentStory = storyService.hasRecentStory(userId);
        if (currentUserHasRecentStory) {
            User foundUser = userService.getUserById(userId);
            // 현재 사용자의 스토리에 대해 확인
            boolean currentUserHasUnreadStories = storyService.hasUnreadStories(userId, userId);

            ResponseFollowerDto currentUserDto =
                    toResponseFollowerDto(foundUser, currentUserHasUnreadStories);
            followingsWithRecentStories.add(currentUserDto);
        }

        return followingsWithRecentStories;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> findFollowingsWhoFollowTarget(Long sourceId, Long targetId) {
        // 소스 사용자의 팔로잉 목록을 가져옴
        List<Long> followingIds = followCrudService.findFollowingIds(sourceId);

        // 팔로잉 중에서 타겟 사용자를 팔로우하는 사람들을 필터링
        return followingIds.stream()
                .filter(followingId -> isFollowing(followingId, targetId))
                .collect(Collectors.toList());
    }

    /**
     * 로그인 사용자와 타겟 사용자 간의 팔로우 상태를 기반으로 DTO 리스트를 생성합니다.
     *
     * @param loginUserFollowingIdSet  로그인 사용자가 팔로우 중인 사용자 ID 세트
     * @param targetUserFollowingIdSet 타겟 사용자의 팔로우 중인 사용자 ID 세트
     * @return 팔로우 상태를 기준으로 한 DTO 리스트
     */
    private List<ResponseFollowerDto> getResponseFollowerDtos(Set<Long> loginUserFollowingIdSet,
                                                              Set<Long> targetUserFollowingIdSet) {
        Set<Long> intersection = new HashSet<>(loginUserFollowingIdSet);
        Set<Long> disjoint = new HashSet<>(targetUserFollowingIdSet);

        // 타겟 유저에 대한 팔로우 유저 중에서 로그인 유저가 팔로우 중인 사람들
        intersection.retainAll(targetUserFollowingIdSet);
        List<ResponseFollowerDto> followIntersection =
                convertUserIdSetToFollowerDtoList(intersection, FOLLOWING);

        // 타겟 유저에 대한 팔로우 유저 중에서 로그인 유저가 팔로우하지 않은 사람들
        disjoint.removeAll(intersection);
        List<ResponseFollowerDto> followDisjoint =
                convertUserIdSetToFollowerDtoList(disjoint, NOT_FOLLOWING);

        return Stream.concat(followIntersection.stream(), followDisjoint.stream())
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID 목록을 세트 형태로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 ID 목록을 세트로 변환한 결과
     */
    private Set<Long> findFollowerIdsAsSet(Long userId) {
        return followCrudService.findFollowerIds(userId)
                .stream().collect(Collectors.toSet());
    }

    /**
     * 사용자 ID 목록을 세트 형태로 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 ID 목록을 세트로 변환한 결과
     */
    private Set<Long> findFollowingIdsAsSet(Long userId) {
        return followCrudService.findFollowingIds(userId)
                .stream().collect(Collectors.toSet());
    }

    /**
     * 사용자 ID와 관련된 채팅 정보를 포함하는 DTO를 생성합니다.
     *
     * @param loginUserId  로그인 사용자 ID
     * @param userId       대상 사용자 ID
     * @param followingIds 로그인 사용자가 팔로우 중인 사용자 ID 목록
     * @param followerIds  로그인 사용자를 팔로우하는 사용자 ID 목록
     * @return 채팅 관련 정보를 포함한 DTO
     */
    private ResponseUserChatDto getResponseUserChatDto(Long loginUserId,
                                                       Long userId,
                                                       List<Long> followingIds,
                                                       List<Long> followerIds) {
        User user = userService.getUserById(userId);
        Integer unreadChatCount = chatService.getUnreadChatCount(loginUserId, userId);
        LastMessageDto lastMessage = chatService.getLastMessage(loginUserId, userId);
        Integer state = getState(userId, followingIds, followerIds);

        return toResponseUserChatDto(user, unreadChatCount, lastMessage, state);
    }
    /**
     * 사용자와의 팔로우 상태를 기반으로 상태 값을 반환합니다.
     *
     * @param userId       사용자 ID
     * @param followingIds 로그인 사용자가 팔로우 중인 사용자 ID 목록
     * @param followerIds  로그인 사용자를 팔로우하는 사용자 ID 목록
     * @return 팔로우 상태 값
     */
    private static Integer getState(Long userId,
                                    List<Long> followingIds,
                                    List<Long> followerIds) {
        Integer state;
        boolean isFollowing = followingIds.contains(userId);
        boolean isFollowedBy = followerIds.contains(userId);

        if (isFollowing && isFollowedBy) {
            state = BOTH_FOLLOWING;
        } else if (isFollowing) {
            state = FOLLOWING_ONLY;
        } else {
            state = FOLLOWED_ONLY;
        }
        return state;
    }

    /**
     * 최근 스토리가 있는 팔로잉 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 최근 스토리가 있는 팔로잉 목록
     */
    private List<ResponseFollowerDto> getFollowingsWithRecentStories(Long userId) {
        return findFollowings(userId).stream()
                .filter(following -> storyService.hasRecentStory(following.getId()))
                .peek(following -> {
                    boolean hasUnreadStories = storyService.hasUnreadStories(following.getId(), userId);
                    following.setHasUnreadStories(hasUnreadStories);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자 ID 목록을 기반으로 팔로워 DTO 리스트를 생성합니다.
     *
     * @param userIdSet 사용자 ID 세트
     * @param follow    팔로우 상태
     * @return 팔로워 DTO 리스트
     */
    private List<ResponseFollowerDto> convertUserIdSetToFollowerDtoList(Set<Long> userIdSet,
                                                                        int follow) {
        return userIdSet.stream()
                .map(userId -> {
                    User user = userService.getUserById(userId);
                    return toResponseFollowerDto(user, follow);
                }).collect(Collectors.toList());
    }
}
