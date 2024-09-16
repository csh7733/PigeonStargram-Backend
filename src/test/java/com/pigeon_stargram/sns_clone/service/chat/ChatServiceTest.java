//package com.pigeon_stargram.sns_clone.service.chat;
//
//import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
//import com.pigeon_stargram.sns_clone.domain.chat.ImageChat;
//import com.pigeon_stargram.sns_clone.domain.chat.LastMessage;
//import com.pigeon_stargram.sns_clone.domain.chat.TextChat;
//import com.pigeon_stargram.sns_clone.domain.chat.UnreadChat;
//import com.pigeon_stargram.sns_clone.dto.chat.internal.NewChatDto;
//import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
//import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseChatHistoryDto;
//import com.pigeon_stargram.sns_clone.repository.chat.ChatRepository;
//import com.pigeon_stargram.sns_clone.repository.chat.LastMessageRepository;
//import com.pigeon_stargram.sns_clone.repository.chat.UnreadChatRepository;
//import com.pigeon_stargram.sns_clone.service.chat.implV2.ChatServiceV2;
//import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ChatServiceTest {
//
//    @Mock
//    ChatRepository chatRepository;
//    @InjectMocks
//    private ChatServiceV2 chatService;
//    @Mock
//    private UnreadChatRepository unreadChatRepository;
//    @Mock
//    private LastMessageRepository lastMessageRepository;
//
//    private ImageChat imageChat;
//    private TextChat textChat;
//    private LastMessage lastMessage;
//
//    private NewChatDto newChatDto;
//
//    @BeforeEach
//    public void setUp() throws NoSuchFieldException, IllegalAccessException {
//        imageChat = ImageChat.builder()
//                .imagePath("imagePath")
//                .fromUserId(1L)
//                .toUserId(2L)
//                .build();
//        textChat = TextChat.builder()
//                .text("text")
//                .fromUserId(1L)
//                .toUserId(2L)
//                .build();
//        lastMessage = LastMessage.builder()
//                .lastMessage("message")
//                .user1Id(1L)
//                .user2Id(2L)
//                .build();
//        // BaserTimeEntity 필드 조작
//        Field createdDate = BaseTimeEntity.class.getDeclaredField("createdDate");
//        createdDate.setAccessible(true);
//        createdDate.set(textChat, LocalDateTime.of(2000, 1, 1, 0, 0));
//        createdDate.set(imageChat, LocalDateTime.of(2000, 1, 1, 0, 0));
//
//        Field modifiedDate = BaseTimeEntity.class.getDeclaredField("modifiedDate");
//        modifiedDate.setAccessible(true);
//        modifiedDate.set(lastMessage, LocalDateTime.of(2000, 1, 1, 0, 0));
//
//        newChatDto = mock(NewChatDto.class);
//    }
//
//    @Test
//    @DisplayName("새로운 이미지 채팅 저장")
//    public void testSaveImage() {
//        //given
//        when(newChatDto.getIsImage()).thenReturn(true);
//        when(newChatDto.toImageEntity()).thenReturn(imageChat);
//        when(chatRepository.save(any(ImageChat.class))).thenReturn(imageChat);
//
//        //when
//        chatService.save(newChatDto);
//
//        //then
//        verify(newChatDto, times(1)).toImageEntity();
//        verify(newChatDto, never()).toTextEntity();
//    }
//
//    @Test
//    @DisplayName("새로운 텍스트 채팅 저장")
//    public void testSaveText() {
//        //given
//        when(newChatDto.getIsImage()).thenReturn(false);
//        when(newChatDto.toTextEntity()).thenReturn(textChat);
//        when(chatRepository.save(any(TextChat.class))).thenReturn(textChat);
//
//        //when
//        chatService.save(newChatDto);
//
//        //then
//        verify(newChatDto, never()).toImageEntity();
//        verify(newChatDto, times(1)).toTextEntity();
//    }
//
//    @Test
//    @DisplayName("dm 기록 가져오기")
//    public void testGetUserChats() {
//        //given
//        when(chatRepository.findTextChatsBetweenUsers(anyLong(), anyLong()))
//                .thenReturn(List.of(textChat));
//
//        //when
//        ResponseChatHistoryDto findChatDto = chatService.getUserChats(1L, 2L).getFirst();
//
//        //then
//        assertThat(textChat.getText()).isEqualTo(findChatDto.getText());
//        assertThat(textChat.getSenderId()).isEqualTo(findChatDto.getFrom());
//        assertThat(textChat.getRecipientId()).isEqualTo(findChatDto.getTo());
//        assertThat(LocalDateTimeUtil.formatTime(textChat.getCreatedDate()))
//                .isEqualTo(findChatDto.getTime());
//    }
//
//    @Test
//    @DisplayName("읽지않은 채팅 숫자 추가 - 기존에 존재")
//    public void testIncreaseUnreadChatCountExist() {
//        //given
//        UnreadChat unReadChat = UnreadChat.builder()
//                .userId(1L)
//                .toUserId(2L)
//                .count(0)
//                .build();
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.of(unReadChat));
//
//        //when
//        Integer count = chatService.increaseUnReadChatCount(1L, 2L);
//
//        //then
//        assertThat(count).isEqualTo(1);
//        verify(unreadChatRepository, times(1))
//                .save(any(UnreadChat.class));
//    }
//
//    @Test
//    @DisplayName("읽지않은 채팅 숫자 추가 - 새로 생성")
//    public void testIncreaseUnreadChatCountNew() {
//        //given
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.empty());
//
//        //when
//        Integer count = chatService.increaseUnReadChatCount(1L, 2L);
//
//        //then
//        assertThat(count).isEqualTo(1);
//        verify(unreadChatRepository, times(1))
//                .save(any(UnreadChat.class));
//    }
//
//    @Test
//    @DisplayName("읽지 않은 채팅 숫자 가져오기 - 기존에 존재")
//    public void testGetUnreadChatCountExist() {
//        //given
//        UnreadChat unreadChat = UnreadChat.builder()
//                .userId(1L)
//                .toUserId(2L)
//                .build();
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.of(unreadChat));
//
//        //when
//        Integer count = chatService.getUnreadChatCount(1L, 2L);
//
//        //then
//        assertThat(count).isEqualTo(0);
//    }
//
//    @Test
//    @DisplayName("읽지 않은 채팅 숫자 가져오기 - 기존에 없음")
//    public void testGetUnreadChatCount() {
//        //given
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.empty());
//
//        //when
//        Integer count = chatService.getUnreadChatCount(1L, 2L);
//
//        //then
//        assertThat(count).isEqualTo(0);
//    }
//
//    @Test
//    @DisplayName("읽지 않은 채팅 숫자 초기화 - 기존에 존재")
//    public void testSetUnreadChatCount0Exist() {
//        //given
//        UnreadChat unreadChat = UnreadChat.builder()
//                .userId(1L)
//                .toUserId(2L)
//                .count(1)
//                .build();
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.of(unreadChat));
//
//        //when
//        chatService.setUnreadChatCount0(1L, 2L);
//
//        //then
//        assertThat(unreadChat.getCount()).isEqualTo(0);
//        verify(unreadChatRepository, times(1)).save(any(UnreadChat.class));
//    }
//
//    @Test
//    @DisplayName("읽지 않은 채팅 숫자 초기화 - 존재하지 않음")
//    public void testSetUnreadChatCount0Empty() {
//        //given
//        when(unreadChatRepository.findByUserIdAndToUserId(anyLong(), anyLong()))
//                .thenReturn(Optional.empty());
//
//        //when
//        chatService.setUnreadChatCount0(1L, 2L);
//
//        //then
//        verify(unreadChatRepository, never()).save(any(UnreadChat.class));
//    }
//
//    @Test
//    @DisplayName("마지막에 받은 메세지 설정 - 기존에 존재")
//    public void testSetLastMessageExist() {
//        //given
//        when(lastMessageRepository.findByUser1IdAndUser2Id(anyLong(), anyLong()))
//                .thenReturn(Optional.of(lastMessage));
//        when(lastMessageRepository.save(any(LastMessage.class))).thenReturn(lastMessage);
//        when(newChatDto.getText()).thenReturn("new message");
//
//        //when
//        LastMessageDto lastMessageDto = chatService.setLastMessage(newChatDto);
//
//        //then
//        assertThat(lastMessage.getLastMessage()).isEqualTo("new message");
//        assertThat(lastMessageDto.getLastMessage()).isEqualTo("new message");
//    }
//
//    @Test
//    @DisplayName("마지막에 받은 메세지 설정 - 새로 생성")
//    public void testSetLastMessageNew() {
//        //given
//        when(lastMessageRepository.findByUser1IdAndUser2Id(anyLong(), anyLong()))
//                .thenReturn(Optional.of(lastMessage));
//        when(lastMessageRepository.save(any(LastMessage.class))).thenReturn(lastMessage);
//        when(newChatDto.getText()).thenReturn("new message");
//
//        //when
//        LastMessageDto lastMessageDto = chatService.setLastMessage(newChatDto);
//
//        //then
//        assertThat(lastMessage.getLastMessage()).isEqualTo("new message");
//        assertThat(lastMessageDto.getLastMessage()).isEqualTo("new message");
//    }
//
//    @Test
//    @DisplayName("마지막에 받은 메세지 가져오기 - 기존에 존재")
//    public void testGetLastMessage() {
//        //given
//        when(lastMessageRepository.findByUser1IdAndUser2Id(anyLong(), anyLong()))
//                .thenReturn(Optional.of(lastMessage));
//
//        //when
//        LastMessageDto lastMessageDto = chatService.getLastMessage(1L, 2L);
//
//        //then
//        assertThat(lastMessageDto.getLastMessage()).isEqualTo(lastMessage.getLastMessage());
//    }
//
//    @Test
//    @DisplayName("마지막에 받은 메세지 가져오기 - 존재하지 않음")
//    public void testGetLastMessageEmpty() {
//        //given
//        when(lastMessageRepository.findByUser1IdAndUser2Id(anyLong(), anyLong()))
//                .thenReturn(Optional.empty());
//
//        //when
//        LastMessageDto lastMessageDto = chatService.getLastMessage(1L, 2L);
//
//        //then
//        assertThat(lastMessageDto.getLastMessage()).isEqualTo("대화 기록 없음");
//    }
//
//}