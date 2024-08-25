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
import com.pigeon_stargram.sns_clone.service.file.FileService;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final FileService fileService;
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
    }

    @PostMapping("/image")
    public String uploadImage(@RequestPart(value = "image") MultipartFile imageFile){

        return fileService.saveFile(imageFile);
    }

    @PostMapping("/story-reply")
    public NewChatDto sendStoryReply(@LoginUser SessionUser loginUser,
                                     @RequestBody NewChatDto chatMessage) {
        Long from = loginUser.getId();
        Long to = chatMessage.getTo();

        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

//        if (!isUserChattingWith(to, from)) {
//            Integer count = chatService.increaseUnReadChatCount(to, from);
//            chatService.sentUnReadChatCountToUser(to, from, count);
//        }
//
//        LastMessageDto lastMessage = chatService.setLastMessage(chatMessage);
//        SendLastMessageDto sendLastMessageDto = buildSendLastMessageDto(from, to, lastMessage);
//        chatService.sentLastMessage(sendLastMessageDto);

        Long[] userIds = chatService.sortAndGet(from, to);

        String destination = "/topic/chat/" + userIds[0] + "/" + userIds[1];
        messagingTemplate.convertAndSend(destination, chatMessage);

        log.info("chatMessage={}", chatMessage);

        return chatMessage;
    }


    @MessageMapping("/chat/{user1Id}/{user2Id}")
    @SendTo("/topic/chat/{user1Id}/{user2Id}")
    public NewChatDto sendMessage(@Payload NewChatDto chatMessage) {

        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

        return chatMessage;
    }

}
