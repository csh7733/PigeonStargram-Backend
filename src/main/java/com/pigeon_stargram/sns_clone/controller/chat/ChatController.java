package com.pigeon_stargram.sns_clone.controller.chat;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.request.RequestOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.*;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.pigeon_stargram.sns_clone.config.WebSocketEventListener.isUserChattingWith;
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
    public List<ResponseChatHistoryDto> getCurrentChatHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return chatService.getUserChats(user1Id, user2Id);
    }

    @GetMapping("/users/{id}/online-status")
    public ResponseOnlineStatusDto getOnlineStatus(@PathVariable Long id) {
        return userService.getOnlineStatus(id);
    }

    @PutMapping("/users/{id}/online-status")
    public void setOnlineStatus(@PathVariable Long id,
                                @RequestBody RequestOnlineStatusDto request) {
        String onlineStatus = request.getOnlineStatus();
        userService.updateOnlineStatus(id, onlineStatus);

        sentOnlineStatus(id, onlineStatus);
    }

    @MessageMapping("/chat/{user1Id}/{user2Id}")
    @SendTo("/topic/chat/{user1Id}/{user2Id}")
    public NewChatDto sendMessage(@Payload NewChatDto chatMessage) {
        Long from = chatMessage.getFrom();
        Long to = chatMessage.getTo();

        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

        if(!isUserChattingWith(to,from)){
            Integer count = chatService.increaseUnReadChatCount(to, from);
            sentUnReadChatCountToUser(to,from,count);
        }

        LastMessageDto lastMessage = chatService.setLastMessage(chatMessage);
        sentLastMessage(from,to,lastMessage);

        return chatMessage;
    }

    public void sentUnReadChatCountToUser(Long toUserId, Long fromUserId, Integer count) {
        String destination = "/topic/users/status/" + toUserId;
        messagingTemplate.convertAndSend(destination, new UnReadChatCountDto(fromUserId,count));
    }

    public void sentLastMessage(Long user1Id, Long user2Id,LastMessageDto lastMessage) {
        String destination1 = "/topic/users/status/" + user1Id;
        String destination2 = "/topic/users/status/" + user2Id;
        messagingTemplate.convertAndSend(destination1, lastMessage);
        messagingTemplate.convertAndSend(destination2, lastMessage);
    }

    public void sentOnlineStatus(Long userId, String onlineStatus) {
        List<ResponseFollowerDto> followers = followService.findFollowers(userId);

        followers.stream()
                .map(ResponseFollowerDto::getId)
                .forEach(followerId -> {
                    String destination = "/topic/users/status/" + followerId;
                    messagingTemplate.convertAndSend(destination, new ResponseOnlineStatusDto(userId, onlineStatus));
                });
    }

}
