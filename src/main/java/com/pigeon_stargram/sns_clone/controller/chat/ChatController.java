package com.pigeon_stargram.sns_clone.controller.chat;

import com.pigeon_stargram.sns_clone.dto.chat.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UnReadChatCountDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
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
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/users")
    public List<UserChatDto> getAllChatPartners() {

        //임시로 현재 유저ID = 15로 설정
        return userService.findAllUsersForChat(15L);
    }

    @GetMapping("/users/{id}")
    public UserChatDto getChatPartner(@PathVariable Long id) {

        return userService.findUserForChat(id);
    }

    @GetMapping("/chats")
    public List<ChatHistoryDto> getCurrentChatHistory(@RequestParam Long user1Id, @RequestParam Long user2Id) {

        return chatService.getUserChats(user1Id, user2Id);
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

}
