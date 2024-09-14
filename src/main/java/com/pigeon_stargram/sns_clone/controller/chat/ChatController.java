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
import static com.pigeon_stargram.sns_clone.dto.chat.ChatDtoConvertor.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;
import static com.pigeon_stargram.sns_clone.util.LogUtil.*;

/**
 * 채팅과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 사용자가 채팅 메시지를 주고받거나, 채팅 파트너 목록을 조회하고 온라인 상태를 관리하는 작업을 수행할 수 있습니다.
 */
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


    /**
     * 두 사용자 간의 채팅 내역을 반환합니다.
     *
     * @param user1Id 첫 번째 사용자의 ID
     * @param user2Id 두 번째 사용자의 ID
     * @param lastFetchedTime 마지막으로 가져온 시간 (선택사항)
     * @param size 가져올 채팅 내역의 수
     * @return 두 사용자 간의 채팅 내역
     */
    @GetMapping("/chats")
    public ResponseChatHistoriesDto getCurrentChatHistory(@RequestParam Long user1Id,
                                                              @RequestParam Long user2Id,
                                                              @RequestParam(required = false) String lastFetchedTime,
                                                              @RequestParam(defaultValue = "10") int size) {

        GetUserChatsDto getUserChatsDto = toGetUserChatsDto(user1Id, user2Id);
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


    /**
     * 채팅에 이미지를 업로드합니다.
     *
     * @param imageFile 업로드할 이미지 파일
     * @return 업로드된 파일의 경로
     */
    @PostMapping("/image")
    public String uploadImage(@RequestPart(value = "image") MultipartFile imageFile){

        return fileService.saveFile(imageFile);
    }

    /**
     * 사용자가 스토리에 대한 답장을 보냅니다.
     *
     * @param loginUser 현재 로그인한 사용자 (세션 정보)
     * @param chatMessage 답장 메시지 (NewChatDto)
     * @return 답장 메시지 (NewChatDto)
     */
    @PostMapping("/story-reply")
    public NewChatDto sendStoryReply(@LoginUser SessionUser loginUser,
                                     @RequestBody NewChatDto chatMessage) {
        Long from = loginUser.getId();
        Long to = chatMessage.getTo();

        // 메시지 저장 및 처리
        processChatMessage(chatMessage, from, to);

        return chatMessage;
    }

    /**
     * WebSocket을 통해 두 사용자 간의 채팅 메시지를 전송합니다.
     *
     * @param chatMessage 전송할 채팅 메시지 (NewChatDto)
     * @param user1Id 첫 번째 사용자의 ID
     * @param user2Id 두 번째 사용자의 ID
     */
    @MessageMapping("/chat/{user1Id}/{user2Id}")
    public void sendMessage(@Payload NewChatDto chatMessage,
                                  @DestinationVariable Long user1Id,
                                  @DestinationVariable Long user2Id) {
        // 메시지 저장 및 처리
        processChatMessage(chatMessage, user1Id, user2Id);
    }

    private void processChatMessage(NewChatDto chatMessage, Long from, Long to) {
        // 메시지의 시간을 설정하고 저장
        chatMessage.setTime(getCurrentFormattedTime());
        chatService.save(chatMessage);

        // Redis 채널에 메시지 발행
        String channel = getChatChannelName(from, to);
        redisService.publishMessage(channel, chatMessage);
    }

    private String getChatChannelName(Long user1Id, Long user2Id) {
        long smallerId = Math.min(user1Id, user2Id);
        long largerId = Math.max(user1Id, user2Id);

        return "chat." + smallerId + "." + largerId;
    }

}
