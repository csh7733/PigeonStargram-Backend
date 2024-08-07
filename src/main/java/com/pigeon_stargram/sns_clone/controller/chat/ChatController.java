package com.pigeon_stargram.sns_clone.controller.chat;

import com.pigeon_stargram.sns_clone.dto.chat.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @GetMapping("/users")
    public List<UserChatDto> getAllChatPartners() {
        log.info("users!!!");

        return userService.findAllUsersForChat();
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
            log.info("not chat with");
        }
        return chatMessage;
    }

}
