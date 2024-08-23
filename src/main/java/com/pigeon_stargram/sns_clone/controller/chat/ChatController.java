package com.pigeon_stargram.sns_clone.controller.chat;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.request.RequestOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.*;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatBuilder;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserBuilder;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.config.WebSocketEventListener.isUserChattingWith;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.*;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final FollowService followService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/users")
    public List<ResponseUserChatDto> getAllChatPartners(@LoginUser SessionUser loginUser) {
        Long userId = loginUser.getId();

        return followService.findPartnersForChat(userId);
    }

    @GetMapping("/users/{id}")
    public ResponseUserChatDto getChatPartner(@PathVariable Long id) {

        return userService.findUserChatById(id);
    }

    @GetMapping("/chats")
    public List<ResponseChatHistoryDto> getCurrentChatHistory(@RequestParam Long user1Id,
                                                              @RequestParam Long user2Id) {
        GetUserChatsDto getUserChatsDto = buildGetUserChatsDto(user1Id, user2Id);
        return chatService.getUserChats(getUserChatsDto);
    }

    @GetMapping("/users/{id}/online-status")
    public ResponseOnlineStatusDto getOnlineStatus(@PathVariable Long id) {
        return userService.getOnlineStatus(id);
    }

    @PutMapping("/users/{id}/online-status")
    public void setOnlineStatus(@PathVariable Long id,
                                @RequestBody RequestOnlineStatusDto request) {
        String onlineStatus = request.getOnlineStatus();
        UpdateOnlineStatusDto updateOnlineStatusDto = buildUpdateOnlineStatusDto(id, onlineStatus);
        userService.updateOnlineStatus(updateOnlineStatusDto);

        sentOnlineStatus(updateOnlineStatusDto);
    }

    @MessageMapping("/chat/{user1Id}/{user2Id}")
    @SendTo("/topic/chat/{user1Id}/{user2Id}")
    public NewChatDto sendMessage(@Payload NewChatDto chatMessage) {
        Long user1Id = chatMessage.getFrom();
        Long user2Id = chatMessage.getTo();

        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

        if (!isUserChattingWith(user2Id, user1Id)) {
            Integer count = chatService.increaseUnReadChatCount(user2Id, user1Id);
            sentUnReadChatCountToUser(user2Id, user1Id, count);
        }

        LastMessageDto lastMessage = chatService.setLastMessage(chatMessage);
        SendLastMessageDto sendLastMessageDto =
                buildSendLastMessageDto(user1Id, user2Id, lastMessage);
        sentLastMessage(sendLastMessageDto);

        return chatMessage;
    }

    public void sentUnReadChatCountToUser(Long toUserId, Long fromUserId, Integer count) {
        String destination = "/topic/users/status/" + toUserId;
        messagingTemplate.convertAndSend(destination, new UnReadChatCountDto(fromUserId, count));
    }

    public void sentLastMessage(SendLastMessageDto dto) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();
        LastMessageDto lastMessage = dto.getLastMessageDto();

        String destination1 = "/topic/users/status/" + user1Id;
        String destination2 = "/topic/users/status/" + user2Id;
        messagingTemplate.convertAndSend(destination1, lastMessage);
        messagingTemplate.convertAndSend(destination2, lastMessage);
    }

    public void sentOnlineStatus(UpdateOnlineStatusDto dto) {
        Long userId = dto.getUserId();
        String onlineStatus = dto.getOnlineStatus();

        List<ResponseFollowerDto> followers = followService.findFollowers(userId);
        List<ResponseFollowerDto> followings = followService.findFollowings(userId);

        Stream.concat(followers.stream(), followings.stream())
                .distinct()
                .map(ResponseFollowerDto::getId)
                .forEach(chatUserId -> {
                    String destination = "/topic/users/status/" + chatUserId;
                    ResponseOnlineStatusDto responseOnlineStatusDto =
                            buildResponseOnlineStatusDto(userId, onlineStatus);
                    messagingTemplate.convertAndSend(destination, responseOnlineStatusDto);
                });
    }

}
