package com.pigeon_stargram.sns_clone.service.notification;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationConvertable;
import com.pigeon_stargram.sns_clone.domain.notification.NotificationType;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyCommentTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyReplyTaggedUsersDto;
import com.pigeon_stargram.sns_clone.dto.notification.response.ResponseNotificationDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.exception.notification.NotificationNotFoundException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.notification.NotificationRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.NotificationWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Spy
    @InjectMocks
    NotificationService notificationService;

    @Mock
    UserService userService;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    NotificationWorker notificationWorker;

    User user;
    Posts post;
    Comment comment;

    List<Long> recipientIds;

    List<NotificationConvertable> notificationConvertables;

    Integer timesSum;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        post = mock(Posts.class);
        comment = mock(Comment.class);

        // 유저 필드 설정
        lenient().when(user.getId()).thenReturn(1L);

        // 게시글, 댓글 작성자의 id를 2로 설정
        lenient().when(post.getUser()).thenReturn(mock(User.class));
        lenient().when(post.getUser().getId()).thenReturn(2L);
        lenient().when(comment.getUser()).thenReturn(mock(User.class));
        lenient().when(comment.getUser().getId()).thenReturn(2L);

        recipientIds = List.of(2L, 3L, 4L);

        notificationConvertables = new ArrayList<>();
        notificationConvertables.add(new AddFollowDto(1L, 2L));
        notificationConvertables.add(new CreatePostDto(user, "post-content", recipientIds));
        notificationConvertables.add(new CreateCommentDto(user, post, "comment-content"));
        notificationConvertables.add(new CreateReplyDto(user, comment, "reply-content", 1L, 1L));
        notificationConvertables.add(new LikePostDto(user, 1L, 2L));
        notificationConvertables.add(new LikeCommentDto(user, 1L, 1L, 2L, 1L));
        notificationConvertables.add(new LikeReplyDto(user, 1L, 1L, 2L, 1L));
        notificationConvertables.add(new NotifyPostTaggedUsersDto(user, "tag-post-content", recipientIds));
        notificationConvertables.add(new NotifyCommentTaggedUsersDto(user, "tag-comment-content", 1L, 1L, recipientIds));
        notificationConvertables.add(new NotifyReplyTaggedUsersDto(user, "tag-reply-content", 1L, 1L, recipientIds));

        timesSum = 0;
    }

    @Test
    @DisplayName("알림 저장 - 성공")
    void testSaveSuccess() {
        //given
        lenient().when(userService.findById(1L)).thenReturn(user);
        when(user.getName()).thenReturn("sender-name");
        for (long i = 2; i <= 4; i++) {
            User recipient = mock(User.class);
            lenient().when(userService.findById(i)).thenReturn(recipient);
        }

        when(notificationRepository.saveAll(anyList()))
                .thenAnswer(new Answer<List<Notification>>() {
                    @Override
                    public List<Notification> answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        return (List<Notification>) args[0];  // 첫 번째 파라미터(리스트)를 그대로 리턴
                    }
                });

        doNothing().when(notificationWorker).enqueue(any(ResponseNotificationDto.class));


        notificationConvertables.forEach(notificationConvertable -> {
            //when
            List<Notification> notifications = notificationService.save(notificationConvertable);

            //then
            notifications.forEach(notification -> {
                assertThat(recipientIds.contains(notification.getRecipient().getId()));
            });

            timesSum += notifications.size();
            verify(notificationWorker, times(timesSum))
                    .enqueue(any(ResponseNotificationDto.class));
        });
    }

    @Test
    @DisplayName("알림 저장 - 송신자 없을시 예외")
    void testSaveUserNotFound() {
        //given
        when(userService.findById(1L)).thenThrow(UserNotFoundException.class);

        notificationConvertables.forEach(notificationConvertable -> {
            //when

            //then
            assertThatThrownBy(() -> {
                notificationService.save(notificationConvertable);
            }).isInstanceOf(UserNotFoundException.class);
        });
    }

    @Test
    @DisplayName("읽지 않은 모든 알림 가져오기")
    void testFindUnreadNotifications() {
        //given
        Notification notification1 = new Notification(
                1L, user, mock(User.class), NotificationType.FOLLOW,
                "message", false, 1L, 2L
        );
        Notification notification2 = new Notification(
                2L, user, mock(User.class), NotificationType.FOLLOW,
                "message", true, 1L, 2L
        );
        List<Notification> notifications = List.of(notification1, notification2);

        when(notificationRepository.findAllByRecipientId(anyLong()))
                .thenReturn(notifications);
        //when
        List<ResponseNotificationDto> unreadNotifications = notificationService.findUnreadNotifications(1L);

        //then
        assertThat(unreadNotifications.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("알림 하나 읽음처리 - 성공")
    void testReadNotificationSuccess() {
        //given
        Notification notification = new Notification(
                1L, user, mock(User.class), NotificationType.FOLLOW,
                "message", false, 1L, 2L
        );
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.of(notification));

        //when
        notificationService.readNotification(1L);

        //then
        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("알림 하나 읽음처리 - 알림 없을시 예외")
    void testReadNotificationNotificationNotFound() {
        //given
        when(notificationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            notificationService.readNotification(1L);
        }).isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    @DisplayName("알림 전체 읽음처리 - 성공")
    void testReadNotificationsSuccess() {
        //given
        Notification notification1 = new Notification(
                1L, user, mock(User.class), NotificationType.FOLLOW,
                "message", false, 1L, 2L
        );
        Notification notification2 = new Notification(
                2L, user, mock(User.class), NotificationType.FOLLOW,
                "message", false, 1L, 2L
        );
        List<Notification> notifications = List.of(notification1, notification2);

        when(notificationRepository.findAllByRecipientId(anyLong()))
                .thenReturn(notifications);

        //when
        notificationService.readNotifications(1L);

        //then
        notifications.forEach(notification -> {
            assertThat(notification.getIsRead()).isTrue();
        });
    }

    @Test
    @DisplayName("태그되어있는 유저에게 알림")
    void testNotifyTaggedUsersTagged() {
        //given
        NotificationConvertable dto = new NotifyPostTaggedUsersDto(user, "content", List.of(1L, 2L));
        doReturn(null).when(notificationService).save(any(NotificationConvertable.class));

        //when
        notificationService.notifyTaggedUsers(dto);

        //then
        verify(notificationService, times(1))
                .save(dto);
    }

    @Test
    @DisplayName("태그되어있지 않을 시 알리지 않음")
    void testNotifyTaggedUsersNotTagged() {
        //given
        NotificationConvertable dto = new NotifyPostTaggedUsersDto(user, "content", List.of());

        //when
        notificationService.notifyTaggedUsers(dto);

        //then
        verify(notificationService, never()).save(dto);
    }

}