package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.DeleteFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final BasicUserService userService;
    private final ChatService chatService;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    public Follow createFollow(AddFollowDto dto) {
        User sender = userService.findById(dto.getSenderId());
        User recipient = userService.findById(dto.getRecipientIds().getFirst());
        log.info("sender ={}, recipient = {}",sender.getId(),recipient.getId());
        followRepository.findBysenderAndRecipient(sender, recipient)
                .ifPresent(follow -> {
                    throw new IllegalArgumentException("이미 팔로우 중입니다.");
                });

        Follow follow = followRepository.save(dto.toEntity(sender, recipient));
        notificationService.save(dto);
        return follow;
    }

    public void deleteFollow(DeleteFollowDto dto){
        User sender = userService.findById(dto.getSenderId());
        User recipient = userService.findById(dto.getRecipientId());
        followRepository.findBysenderAndRecipient(sender, recipient)
                .ifPresent(followRepository::delete);
    }

    public List<ResponseFollowerDto> findFollowers(Long userId) {
        User user = userService.findById(userId);

        return followRepository.findByRecipient(user).stream()
                .map(Follow::getSender)
                .map(sender -> new ResponseFollowerDto(sender, 1))
                .toList();
    }

    public List<ResponseFollowerDto> findFollowers(Long currentUserId, Long userId) {
        User user = userService.findById(userId);
        User currentUser = userService.findById(currentUserId);

        return followRepository.findByRecipient(user).stream()
                .map(Follow::getSender)
                .map(sender -> {
                    Integer isFollowing = isFollowing(currentUser, sender) ? 1 : 2;
                    return new ResponseFollowerDto(sender, isFollowing);
                })
                .toList();
    }

    public Boolean isFollowing(User source, User target) {
        return followRepository.findByRecipient(target)
                .stream()
                .anyMatch(follow -> follow.getSender().equals(source));
    }

    private Boolean checkMutualFollow(User user1, User user2) {
        return isFollowing(user1, user2) && isFollowing(user2, user1);
    }

    public List<Long> findFollows(Long userId) {
        User user = userService.findById(userId);

        return followRepository.findByRecipient(user).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(follow -> follow.getSender().getId())
                .toList();
    }

    public List<ResponseFollowerDto> findFollowings(Long currentUserId, Long userId) {
        User user = userService.findById(userId);
        User currentUser = userService.findById(currentUserId);

        return followRepository.findBySender(user).stream()
                .map(Follow::getRecipient)
                .map(recipient -> {
                    Integer isFollowing = isFollowing(currentUser, recipient) ? 1 : 2;
                    return new ResponseFollowerDto(recipient, isFollowing);
                })
                .toList();
    }

    public List<User> findAll(){
        return followRepository.findAll().stream()
                .map(Follow::getRecipient)
                .collect(Collectors.toList());
    }

    public List<ResponseUserChatDto> findPartnersForChat(Long currentUserId) {
        User currentUser = userService.findById(currentUserId);

        List<User> followers = followRepository.findByRecipient(currentUser)
                .stream()
                .map(Follow::getSender)
                .collect(Collectors.toList());

        List<User> following = followRepository.findBySender(currentUser)
                .stream()
                .map(Follow::getRecipient)
                .collect(Collectors.toList());

        return Stream.concat(followers.stream(), following.stream())
                .distinct()
                .map(user -> {
                    Integer unReadChatCount = chatService.getUnreadChatCount(currentUserId, user.getId());
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

                    return new ResponseUserChatDto(user, unReadChatCount, lastMessage, state);
                })
                .sorted(Comparator.comparing(ResponseUserChatDto::getLastMessage, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }


    public Long countFollowings(User user) {
        return followRepository.countBySender(user);
    }

    public Long countFollowers(User user) {
        return followRepository.countByRecipient(user);
    }
}
