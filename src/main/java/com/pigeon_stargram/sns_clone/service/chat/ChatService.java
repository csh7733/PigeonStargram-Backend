package com.pigeon_stargram.sns_clone.service.chat;

import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.GetUserChatsDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.internal.SendLastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UnReadChatCountDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.event.UserConnectEvent;
import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.config.WebSocketEventListener.isUserChattingWith;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildResponseOnlineStatusDto;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildSendLastMessageDto;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentFormattedTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRepository chatRepository;
    private final UnreadChatRepository unreadChatRepository;
    private final LastMessageRepository lastMessageRepository;

    public void save(NewChatDto dto){

        if(dto.getIsImage()) chatRepository.save(dto.toImageEntity());
        else chatRepository.save(dto.toTextEntity());

        Long user1Id = dto.getFrom();
        Long user2Id = dto.getTo();

        if (!isUserChattingWith(user2Id, user1Id)) {
            Integer count = increaseUnReadChatCount(user2Id, user1Id);
            sentUnReadChatCountToUser(user2Id, user1Id, count);
        }

        LastMessageDto lastMessage = setLastMessage(dto);
        SendLastMessageDto sendLastMessageDto =
                buildSendLastMessageDto(user1Id, user2Id, lastMessage);
        sentLastMessage(sendLastMessageDto);
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

    // 테스트 데이터 주입에서만 사용
    public void saveChat(Long fromUserId, Long toUserId, String text) {
        TextChat textChat = TextChat.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .text(text)
                .build();
        chatRepository.save(textChat);
    }

    /**
     * TODO : 이미지 채팅 구현
     */
    public void saveImageChat(Long fromUserId, Long toUserId, String imagePath) {
        ImageChat imageChat = ImageChat.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .imagePath(imagePath)
                .build();
        chatRepository.save(imageChat);
    }

    public List<ResponseChatHistoryDto> getUserChats(GetUserChatsDto dto) {
        Long user1Id = dto.getUser1Id();
        Long user2Id = dto.getUser2Id();

        List<TextChat> textChats = chatRepository.findTextChatsBetweenUsers(user1Id, user2Id);
        List<ImageChat> imageChats = chatRepository.findImageChatsBetweenUsers(user1Id, user2Id);

        List<ResponseChatHistoryDto> chatHistoryDtos = new ArrayList<>();

        chatHistoryDtos.addAll(textChats.stream()
                .map(ChatBuilder::buildResponseChatHistoryDto)
                .collect(Collectors.toList()));

        chatHistoryDtos.addAll(imageChats.stream()
                .map(ChatBuilder::buildResponseChatHistoryDto)
                .collect(Collectors.toList()));

        chatHistoryDtos.sort(Comparator.comparing(ResponseChatHistoryDto::getTime));

        return chatHistoryDtos;
    }


    public Integer increaseUnReadChatCount(Long userId,Long toUserId){
        UnreadChat unReadChat = unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .orElse(new UnreadChat(userId, toUserId));

        Integer count = unReadChat.incrementCount();
        unreadChatRepository.save(unReadChat);
        return count;
    }

    public Integer getUnreadChatCount(Long userId, Long toUserId) {
        return unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .map(UnreadChat::getCount)
                .orElse(0);
    }

    public void setUnreadChatCount0(Long userId, Long toUserId) {
        unreadChatRepository.findByUserIdAndToUserId(userId, toUserId)
                .ifPresent(unreadChat -> {
                    unreadChat.resetCount();
                    unreadChatRepository.save(unreadChat);
                });
    }

    public LastMessageDto setLastMessage(NewChatDto chatMessage) {
        Long user1Id = chatMessage.getFrom();
        Long user2Id = chatMessage.getTo();

        Long[] userIds = sortAndGet(user1Id, user2Id);
        String lastMessageText = chatMessage.getIsImage() ? "사진" : chatMessage.getText();

        LastMessage lastMessageEntity = lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .orElse(new LastMessage(userIds[0], userIds[1], lastMessageText));

        lastMessageEntity.update(lastMessageText);

        //실시간 반영
        LastMessageDto lastMessageDto = new LastMessageDto(lastMessageRepository.save(lastMessageEntity));
        lastMessageDto.setTime(getCurrentFormattedTime());

        return lastMessageDto;
    }

    public LastMessageDto getLastMessage(Long user1Id, Long user2Id) {
        Long[] userIds = sortAndGet(user1Id, user2Id);

        return lastMessageRepository.findByUser1IdAndUser2Id(userIds[0], userIds[1])
                .map(LastMessageDto::new)
                .orElseGet(LastMessageDto::new);
    }

    @EventListener
    public void handleUserConnect(UserConnectEvent event) {
        Long userId = event.getUserId();
        Long partnerUserId = event.getPartnerUserId();
        setUnreadChatCount0(userId,partnerUserId);
    }

    public Long[] sortAndGet(Long user1Id, Long user2Id) {
        Long[] userIds = {user1Id, user2Id};
        Arrays.sort(userIds);
        return userIds;
    }
}
