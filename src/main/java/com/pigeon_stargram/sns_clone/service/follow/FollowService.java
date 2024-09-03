package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.DeleteFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowersDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.FindFollowingsDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.GetNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.ToggleNotificationEnabledDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.exception.follow.FollowExistException;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.user.UserBuilder;
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

import static com.pigeon_stargram.sns_clone.service.follow.FollowBuilder.*;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final FollowCrudService followCrudService;

    private final UserService userService;
    private final ChatService chatService;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final StoryService storyService;

    public Follow createFollow(AddFollowDto dto) {
        Long senderId = dto.getSenderId();
        Long recipientId = dto.getRecipientId();

        if (followCrudService.findFollowers(recipientId).contains(senderId)) {
            throw new FollowExistException("이미 팔로우 중입니다.");
        }

        User sender = userService.findById(senderId);
        User recipient = userService.findById(recipientId);
        log.info("sender ={}, recipient = {}",sender.getId(),recipient.getId());

        Follow follow = buildFollow(sender, recipient);
        Follow save = followCrudService.save(follow);

        notificationService.send(dto);

        return save;
    }

    public void deleteFollow(DeleteFollowDto dto){
        Long senderId = dto.getSenderId();
        Long recipientId = dto.getRecipientId();

        followCrudService.deleteFollowBySenderIdAndRecipientId(senderId, recipientId);
    }

    public List<ResponseFollowerDto> findFollowings(Long userId) {
        return followRepository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)
                .map(recipient -> new ResponseFollowerDto(recipient, 1))
                .collect(Collectors.toList());
    }

    public List<ResponseFollowerDto> findFollowers(FindFollowersDto dto) {
        Set<Long> targetUserFollowerIdSet =
                followCrudService.findFollowers(dto.getUserId())
                        .stream().collect(Collectors.toSet());
        Set<Long> loginUserFollowingIdSet =
                followCrudService.findFollowings(dto.getLoginUserId())
                        .stream().collect(Collectors.toSet());

        // 타겟유저를 팔로우 하는 사람 중, 내가 팔로우중인 사람
        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowerIdSet);
    }


    public List<ResponseFollowerDto> findFollowings(FindFollowingsDto dto) {
        Set<Long> targetUserFollowingIdSet =
                followCrudService.findFollowings(dto.getUserId())
                        .stream().collect(Collectors.toSet());
        Set<Long> loginUserFollowingIdSet =
                followCrudService.findFollowings(dto.getLoginUserId())
                        .stream().collect(Collectors.toSet());

        return getResponseFollowerDtos(loginUserFollowingIdSet, targetUserFollowingIdSet);
    }

    private List<ResponseFollowerDto> getResponseFollowerDtos(Set<Long> loginUserFollowingIdSet,
                                                              Set<Long> targetUserFollowingIdSet) {
        Set<Long> intersection = new HashSet<>(loginUserFollowingIdSet);
        Set<Long> disjoint = new HashSet<>(targetUserFollowingIdSet);

        // 타겟유저에 대한 팔로우 유저 중, 로그인 유저가 팔로우중인 사람
        intersection.retainAll(targetUserFollowingIdSet);
        List<ResponseFollowerDto> followIntersection = convertUserIdSetToFollowerDtoList(intersection, 1);

        // 타겟유저에 대한 팔로우 유저 중, 로그인 유저가 팔로우 하지 않은 사람
        disjoint.removeAll(intersection);
        List<ResponseFollowerDto> followDisjoint = convertUserIdSetToFollowerDtoList(disjoint, 2);

        return Stream.concat(followIntersection.stream(), followDisjoint.stream())
                .collect(Collectors.toList());
    }

    private List<ResponseFollowerDto> convertUserIdSetToFollowerDtoList(Set<Long> userIdSet,
                                                                        int follow) {
        return userIdSet.stream()
                .map(userId -> {
                    User user = userService.findById(userId);
                    return buildResponseFollowerDto(user, follow);
                }).collect(Collectors.toList());
    }

    public Boolean isFollowing(Long sourceId, Long targetId) {
        return followCrudService.findFollowers(targetId)
                .contains(sourceId);
    }

    private Boolean checkMutualFollow(Long user1Id, Long user2Id) {
        return isFollowing(user1Id, user2Id) && isFollowing(user2Id, user1Id);
    }

    public List<Long> findFollows(Long userId) {
        return followRepository.findByRecipientId(userId).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(follow -> follow.getSender().getId())
                .collect(Collectors.toList());
    }


    public List<ResponseUserChatDto> findPartnersForChat(Long currentUserId) {
        List<User> followers = followRepository.findByRecipientId(currentUserId).stream()
                .map(Follow::getSender)
                .collect(Collectors.toList());

        List<User> following = followRepository.findBySenderId(currentUserId).stream()
                .map(Follow::getRecipient)
                .collect(Collectors.toList());

        return Stream.concat(followers.stream(), following.stream())
                .distinct()
                .map(user -> {
                    Integer unreadChatCount = chatService.getUnreadChatCount(currentUserId, user.getId());
                    LastMessageDto lastMessage = chatService.getLastMessage(currentUserId, user.getId());

                    Integer state;
                    boolean isFollowing = following.contains(user);
                    boolean isFollowedBy = followers.contains(user);

                    if (isFollowing && isFollowedBy) {
                        state = 2;
                    } else if (isFollowing) {
                        state = 1;
                    } else {
                        state = 0;
                    }

                    return buildResponseUserChatDto(
                            user, unreadChatCount, lastMessage, state);
                })
                .sorted(Comparator.comparing(
                        ResponseUserChatDto::getLastMessage,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public Long countFollowings(Long userId) {
        return followRepository.countBySenderId(userId);
    }

    public Long countFollowers(Long userId) {
        return followRepository.countByRecipientId(userId);
    }

    public void toggleNotificationEnabled(ToggleNotificationEnabledDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long targetUserId = dto.getTargetUserId();
        followRepository.findBySenderIdAndRecipientId(loginUserId, targetUserId)
                .ifPresent(Follow::toggleNotificationEnabled);
    }

    public Boolean getNotificationEnabled(GetNotificationEnabledDto dto) {
        Long senderId = dto.getLoginUserId();
        Long recipientId = dto.getTargetUserId();
        return followRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Follow::getIsNotificationEnabled)
                .orElse(false);
    }

    public List<ResponseFollowerDto> findMeAndFollowingsWithRecentStories(Long userId) {
        log.info("followings={}", findFollowings(userId));
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
            boolean currentUserHasUnreadStories = storyService.hasUnreadStories(userId, userId);
            ResponseFollowerDto currentUserDto = new ResponseFollowerDto(userService.findById(userId), 1);
            currentUserDto.setHasUnreadStories(currentUserHasUnreadStories);
            followingsWithRecentStories.add(currentUserDto);
        }

        return followingsWithRecentStories;
    }


}
