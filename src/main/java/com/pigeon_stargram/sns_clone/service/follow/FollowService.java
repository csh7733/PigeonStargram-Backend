package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.follow.FollowFactory;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.DeleteFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowersDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowingsDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.GetNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.ToggleNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter;
import com.pigeon_stargram.sns_clone.exception.follow.FollowExistException;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
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
import static com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter.*;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.*;
import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowCrudService followCrudService;
    private final UserService userService;
    private final ChatService chatService;
    private final NotificationService notificationService;
    private final StoryService storyService;

    public List<ResponseFollowerDto> findFollowings(Long userId) {
        return followCrudService.findFollowingIds(userId).stream()
                .map(userService::getUserById)
                .map(recipient -> toResponseFollowerDto(recipient, false))
                .collect(Collectors.toList());
    }

    public void createFollow(AddFollowDto dto) {
        Long senderId = dto.getSenderId();
        Long recipientId = dto.getRecipientId();

        if (followCrudService.findFollowerIds(recipientId).contains(senderId)) {
            throw new FollowExistException("이미 팔로우 중입니다.");
        }

        User sender = userService.getUserById(senderId);
        User recipient = userService.getUserById(recipientId);

        Follow follow = FollowFactory.createFollow(sender, recipient);
        followCrudService.save(follow);

        dto.setSenderName(sender.getName());
        notificationService.sendToSplitWorker(dto);
    }

    public void deleteFollow(DeleteFollowDto dto){
        followCrudService.deleteFollowBySenderIdAndRecipientId(dto.getSenderId(), dto.getRecipientId());
    }

    public List<Long> getFollowerIds(Long userId) {
        return followCrudService.findFollowerIds(userId);
    }

    public List<ResponseFollowerDto> findFollowers(FindFollowersDto dto) {
        Set<Long> targetUserFollowerIdSet =
                followCrudService.findFollowerIds(dto.getUserId())
                        .stream().collect(Collectors.toSet());
        Set<Long> loginUserFollowingIdSet =
                followCrudService.findFollowingIds(dto.getLoginUserId())
                        .stream().collect(Collectors.toSet());

        // 타겟유저를 팔로우 하는 사람 중, 내가 팔로우중인 사람
        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowerIdSet);
    }


    public List<ResponseFollowerDto> findFollowings(FindFollowingsDto dto) {
        Set<Long> targetUserFollowingIdSet = findFollowingIdsAsSet(dto.getUserId());
        Set<Long> loginUserFollowingIdSet = findFollowingIdsAsSet(dto.getLoginUserId());

        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowingIdSet);
    }

    public Boolean isFollowing(Long sourceId, Long targetId) {
        return followCrudService.findFollowerIds(targetId)
                .contains(sourceId);
    }

    public List<Long> findFollows(Long userId) {
        return followCrudService.findNotificationEnabledIds(userId);
    }

    public List<ResponseUserChatDto> findPartnersForChat(Long currentUserId) {
        List<Long> followerIds = followCrudService.findFollowerIds(currentUserId);
        List<Long> followingIds = followCrudService.findFollowingIds(currentUserId);

        return Stream.concat(followerIds.stream(), followingIds.stream())
                .distinct()
                .map(userId -> {
                    Integer unreadChatCount = chatService.getUnreadChatCount(currentUserId, userId);
                    LastMessageDto lastMessage = chatService.getLastMessage(currentUserId, userId);

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

                    User user = userService.getUserById(userId);
                    return toResponseUserChatDto(user, unreadChatCount, lastMessage, state);
                })
                .sorted(Comparator.comparing(
                        ResponseUserChatDto::getLastMessage,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public Long countFollowings(Long userId) {
        return (long) followCrudService.findFollowingIds(userId).size();
    }

    public Long countFollowers(Long userId) {
        return (long) followCrudService.findFollowerIds(userId).size();
    }

    public void toggleNotificationEnabled(ToggleNotificationEnabledDto dto) {
       followCrudService.toggleNotificationEnabled(dto.getLoginUserId(), dto.getTargetUserId());
    }

    public Boolean getNotificationEnabled(GetNotificationEnabledDto dto) {
        return followCrudService.findNotificationEnabledIds(dto.getTargetUserId())
                .contains(dto.getLoginUserId());
    }

    public List<ResponseFollowerDto> findMeAndFollowingsWithRecentStories(Long userId) {
        List<ResponseFollowerDto> followingsWithRecentStories = findFollowings(userId).stream()
                .filter(following -> storyService.hasRecentStory(following.getId()))
                .peek(following -> {
                    boolean hasUnreadStories = storyService.hasUnreadStories(following.getId(), userId);
                    following.setHasUnreadStories(hasUnreadStories);
                })
                .collect(Collectors.toList());

        // 현재 사용자가 최근 스토리가 있는지 확인
        boolean currentUserHasRecentStory = storyService.hasRecentStory(userId);
        if (currentUserHasRecentStory) {
            User foundUser = userService.getUserById(userId);
            // 내 스토리에 대해 확인
            boolean currentUserHasUnreadStories = storyService.hasUnreadStories(userId, userId);

            ResponseFollowerDto currentUserDto =
                    toResponseFollowerDto(foundUser, currentUserHasUnreadStories);
            followingsWithRecentStories.add(currentUserDto);
        }

        return followingsWithRecentStories;
    }

    // 유명 사용자 여부를 판별하는 메서드
    public Boolean isFamousUser(Long userId) {
        Long followerCount = countFollowers(userId);
        return followerCount >= FAMOUS_USER_THRESHOLD;
    }

    public List<Long> findFollowingsWhoFollowTarget(Long sourceId, Long targetId) {
        // sourceId의 팔로잉 목록을 가져옴
        List<Long> followingIds = followCrudService.findFollowingIds(sourceId);

        // 팔로잉 중에서 targetId를 팔로우하는 사람들을 필터링
        return followingIds.stream()
                .filter(followingId -> isFollowing(followingId, targetId))  // 해당 사용자가 targetId를 팔로우하는지 확인
                .collect(Collectors.toList());
    }

    private List<ResponseFollowerDto> getResponseFollowerDtos(Set<Long> loginUserFollowingIdSet,
                                                              Set<Long> targetUserFollowingIdSet) {
        Set<Long> intersection = new HashSet<>(loginUserFollowingIdSet);
        Set<Long> disjoint = new HashSet<>(targetUserFollowingIdSet);

        // 타겟유저에 대한 팔로우 유저 중, 로그인 유저가 팔로우중인 사람
        intersection.retainAll(targetUserFollowingIdSet);
        List<ResponseFollowerDto> followIntersection =
                convertUserIdSetToFollowerDtoList(intersection, FOLLOWING);

        // 타겟유저에 대한 팔로우 유저 중, 로그인 유저가 팔로우 하지 않은 사람
        disjoint.removeAll(intersection);
        List<ResponseFollowerDto> followDisjoint =
                convertUserIdSetToFollowerDtoList(disjoint, NOT_FOLLOWING);

        return Stream.concat(followIntersection.stream(), followDisjoint.stream())
                .collect(Collectors.toList());
    }

    private Set<Long> findFollowerIdsAsSet(Long userId) {
        return followCrudService.findFollowerIds(userId)
                .stream().collect(Collectors.toSet());
    }

    private Set<Long> findFollowingIdsAsSet(Long userId) {
        return followCrudService.findFollowingIds(userId)
                .stream().collect(Collectors.toSet());
    }

    private List<ResponseFollowerDto> convertUserIdSetToFollowerDtoList(Set<Long> userIdSet,
                                                                        int follow) {
        return userIdSet.stream()
                .map(userId -> {
                    User user = userService.getUserById(userId);
                    return buildResponseFollowerDto(user, follow);
                }).collect(Collectors.toList());
    }

}
