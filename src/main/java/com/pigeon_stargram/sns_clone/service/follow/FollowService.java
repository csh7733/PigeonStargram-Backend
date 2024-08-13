package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.DeleteFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final UserService userService;
    private final ChatService chatService;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    public Follow createFollow(AddFollowDto dto) {
        User sender = userService.findById(dto.getSenderId());
        User recipient = userService.findById(dto.getRecipientIds().getFirst());
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

    public List<FollowerDto> findFollowers(Long userId) {
        User user = userService.findById(userId);

        return followRepository.findByRecipient(user).stream()
                .map(Follow::getSender)
                .map(sender -> new FollowerDto(sender, 1))
                .toList();
    }

    public List<Follow> findFollows(Long userId) {
        User user = userService.findById(userId);

        return followRepository.findByRecipient(user).stream()
                .toList();
    }

    public List<FollowerDto> findFollowings(Long userId) {
        User user = userService.findById(userId);
        return followRepository.findBySender(user).stream()
                .map(Follow::getRecipient)
                .map(recipient -> new FollowerDto(recipient, 1))
                .toList();
    }

    public List<User> findAll(){
        return followRepository.findAll().stream()
                .map(Follow::getRecipient)
                .collect(Collectors.toList());
    }

    public List<UserChatDto> findFollowersForChat(Long currentUserId) {
        User currentUser = userService.findById(currentUserId);

        return followRepository.findByRecipient(currentUser).stream()
                .map(Follow::getSender)
                .map(user -> {
                    Integer unReadChatCount = chatService.getUnreadChatCount(currentUserId, user.getId());
                    LastMessageDto lastMessage = chatService.getLastMessage(currentUserId, user.getId());

                    return new UserChatDto(user, unReadChatCount, lastMessage);
                })
                .sorted(Comparator.comparing(UserChatDto::getLastMessage, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
}
