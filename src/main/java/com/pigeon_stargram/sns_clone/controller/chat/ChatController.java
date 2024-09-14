package com.pigeon_stargram.sns_clone.controller.chat;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.request.RequestOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.*;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.follow.FollowServiceV2;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.*;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final FollowServiceV2 followService;
    private final UserService userService;
    private final FileService fileService;
    private final RedisService redisService;

    /**
     * 로그인한 사용자의 모든 채팅 파트너 목록을 반환합니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @return 로그인한 사용자의 채팅 파트너 목록 (ResponseUserChatDto 리스트)
     */
    @GetMapping("/users")
    public List<ResponseUserChatDto> getAllChatPartners(@LoginUser SessionUser loginUser) {
        logControllerMethod("getAllChatPartners", loginUser);

        return followService.findPartnersForChat(loginUser.getId());
    }

    /**
     * 특정 ID에 해당하는 채팅 파트너의 정보를 반환합니다.
     *
     * @param id 채팅 파트너의 사용자 ID
     * @return 조회된 채팅 파트너 정보 (ResponseUserChatDto)
     */
    @GetMapping("/users/{id}")
    public ResponseUserChatDto getChatPartner(@PathVariable Long id) {
        logControllerMethod("getChatPartner", id);

        return userService.getUserChatById(id);
    }

    @GetMapping("/chats")
    public ResponseChatHistoriesDto getCurrentChatHistory(@RequestParam Long user1Id,
                                                              @RequestParam Long user2Id,
                                                              @RequestParam(required = false) String lastFetchedTime,
                                                              @RequestParam(defaultValue = "10") int size) {

        GetUserChatsDto getUserChatsDto = buildGetUserChatsDto(user1Id, user2Id);
        return chatService.getUserChats(getUserChatsDto, lastFetchedTime, size);
    }

    /**
     * 특정 사용자 ID에 대한 온라인 상태를 조회합니다.
     *
     * @param id 온라인 상태를 조회할 사용자 ID
     * @return 조회된 사용자의 온라인 상태 정보 (ResponseOnlineStatusDto)
     */
    @GetMapping("/users/{id}/online-status")
    public ResponseOnlineStatusDto getOnlineStatus(@PathVariable Long id) {
        logControllerMethod("getOnlineStatus", id);

        return userService.getOnlineStatus(id);
    }

    /**
     * 특정 사용자 ID의 온라인 상태를 업데이트합니다.
     *
     * @param id 온라인 상태를 변경할 사용자 ID
     * @param request 온라인 상태 갱신 요청 데이터 (RequestOnlineStatusDto)
     */
    @PutMapping("/users/{id}/online-status")
    public void updateOnlineStatus(@PathVariable Long id,
                                   @RequestBody RequestOnlineStatusDto request) {
        logControllerMethod("setOnlineStatus", id, request);

        UpdateOnlineStatusDto dto = toUpdateOnlineStatusDto(id, request.getOnlineStatus());
        userService.updateOnlineStatus(dto);
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

        String channel = getChatChannelName(from, to);
        redisService.publishMessage(channel,chatMessage);

        return chatMessage;
    }


    @MessageMapping("/chat/{user1Id}/{user2Id}")
    public void sendMessage(@Payload NewChatDto chatMessage,
                                  @DestinationVariable Long user1Id,
                                  @DestinationVariable Long user2Id) {
        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

        String channel = getChatChannelName(user1Id, user2Id);
        redisService.publishMessage(channel,chatMessage);
    }

    private String getChatChannelName(Long user1Id, Long user2Id) {
        long smallerId = Math.min(user1Id, user2Id);
        long largerId = Math.max(user1Id, user2Id);

        return "chat." + smallerId + "." + largerId;
    }

}
