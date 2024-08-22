package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyLikeDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Spy
    @InjectMocks
    ReplyService replyService;

    @Mock
    NotificationService notificationService;

    @Mock
    ReplyRepository replyRepository;
    @Mock
    ReplyLikeRepository replyLikeRepository;

    User user;
    Post post;
    Comment comment;
    Reply reply;
    ReplyLike replyLike;

    List<Reply> replies = new ArrayList<>();
    List<ResponseReplyDto> replyDtos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = mock(User.class);
        post = mock(Post.class);
        comment = mock(Comment.class);
        reply = mock(Reply.class);
        replyLike = mock(ReplyLike.class);

        for (int i = 0; i < 3; i++) {
            replies.add(mock(Reply.class));
            replyDtos.add(mock(ResponseReplyDto.class));
        }
    }

    @Test
    @DisplayName("답글 id로 엔티티 조회 - 성공")
    void testGetReplyEntitySuccess() {
        //given
        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(reply));

        //when
        Reply replyEntity = replyService.getReplyEntity(1L);

        //then
        assertThat(replyEntity).isEqualTo(reply);
    }

    @Test
    @DisplayName("답글 id로 엔티티 조회 - 대댓을 찾지 못함")
    void testGetReplyEntityPostNotFound() {
        //given
        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            replyService.getReplyEntity(1L);
        }).isInstanceOf(ReplyNotFoundException.class);
    }
    
    @Test
    @DisplayName("댓글 id로 모든 답글 가져오기")
    void testGetReplyByUser() {
        // Given
        when(replies.get(0).getId()).thenReturn(2L);
        when(replies.get(1).getId()).thenReturn(1L);
        when(replies.get(2).getId()).thenReturn(3L);
        when(replyRepository.findByCommentId(anyLong())).thenReturn(replies);

        doReturn(new ResponseReplyDto()).when(replyService).getCombinedReply(1L);
        doReturn(new ResponseReplyDto()).when(replyService).getCombinedReply(2L);
        doReturn(new ResponseReplyDto()).when(replyService).getCombinedReply(3L);

        // When
        List<ResponseReplyDto> result = replyService.getReplyDtosByCommentId(1L);

        // Then
        assertThat(result.size()).isEqualTo(3);
        verify(replyService, times(1)).getCombinedReply(1L);
        verify(replyService, times(1)).getCombinedReply(2L);
        verify(replyService, times(1)).getCombinedReply(3L);

    }

    @Test
    @DisplayName("답글 id로 ReplyContent, ReplyLike, Reply 가져오기")
    void testGetCombinedReply() {
        //given
        //replyContent
        when(reply.getId()).thenReturn(1L);
        when(reply.getUser()).thenReturn(user);
        when(reply.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(reply.getContent()).thenReturn("content");

        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(reply));

        //replyLike
        when(replyLikeRepository.countByReplyId(anyLong()))
                .thenReturn(5);

        //when
        ResponseReplyDto combinedReplyDto = replyService.getCombinedReply(1L);

        //then
        assertThat(combinedReplyDto.getId()).isEqualTo(reply.getId());
        assertThat(combinedReplyDto.getProfile().getName())
                .isEqualTo(reply.getUser().getName());
        assertThat(combinedReplyDto.getData().getComment())
                .isEqualTo(reply.getContent());
    }

    @Test
    @DisplayName("ReplyContent 가져오기")
    void testGetReplyContent() {
        //given
        when(reply.getId()).thenReturn(1L);
        when(reply.getUser()).thenReturn(user);
        when(reply.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(reply.getContent()).thenReturn("content");

        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(reply));

        //when
        ReplyContentDto replyContent = replyService.getReplyContent(1L);

        //then
        assertThat(replyContent.getComment()).isEqualTo(reply.getContent());
        assertThat(replyContent.getId()).isEqualTo(reply.getId());
        assertThat(replyContent.getProfile().getId()).isEqualTo(reply.getUser().getId());
    }

    @Test
    @DisplayName("댓글 좋아요 가져오기")
    void testGetReplysLike() {
        //given
        when(replyLikeRepository.countByReplyId(anyLong()))
                .thenReturn(5);

        //when
        ReplyLikeDto replyLikeDto = replyService.getReplyLike(1L);

        //then
        assertThat(replyLikeDto.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("답글 생성")
    void testCreateReply() {
        //given
        CreateReplyDto createReplyDto = new CreateReplyDto(user, comment, "content", 1L, 1L);

        when(comment.getUser()).thenReturn(mock(User.class));
        when(comment.getUser().getId()).thenReturn(1L);

        when(notificationService.send(createReplyDto))
                .thenReturn(List.of());
        when(replyRepository.save(any(Reply.class))).thenReturn(reply);

        //when
        Reply createReply = replyService.createReply(createReplyDto);

        //then
        assertThat(createReplyDto.getRecipientIds().getFirst())
                .isEqualTo(comment.getUser().getId());
        assertThat(createReply).isEqualTo(reply);
    }

    @Test
    @DisplayName("답글 수정")
    void testEditReply() {
        //given
        Reply editReply = new Reply(user, comment, "old-content");
        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(editReply));

        //when
        replyService.editReply(1L, "new-content");

        //then
        assertThat(editReply.getContent()).isEqualTo("new-content");
    }

    @Test
    @DisplayName("답글 하나 삭제")
    void testDeleteReply() {
        //given
        doNothing().when(replyRepository).deleteById(anyLong());

        //when
        replyService.deleteReply(1L);

        //then
        verify(replyRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("댓글 id로 답글 전체 삭제")
    void testDeleteAllRepliesByCommentId() {
        //given
        when(replyRepository.findByCommentId(anyLong()))
                .thenReturn(replies);
        doNothing().when(replyRepository).deleteAll(anyList());

        //when
        replyService.deleteAllRepliesByCommentId(1L);

        //then
        verify(replyRepository, times(1))
                .deleteAll(replies);
    }

    @Test
    @DisplayName("답글에 좋아요 - 기존에 존재시 삭제")
    void testLikeReplyExist() {
        //given
        LikeReplyDto likeReplyDto = new LikeReplyDto(user, 1L, 1L, 2L, 1L);
        when(reply.getUser()).thenReturn(mock(User.class));
        when(reply.getUser().getId()).thenReturn(2L);

        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(reply));
        when(replyLikeRepository.findByUserIdAndReplyId(anyLong(), anyLong()))
                .thenReturn(Optional.of(replyLike));

        //when
        replyService.likeReply(likeReplyDto);

        //then
        verify(replyLikeRepository, times(1))
                .delete(replyLike);
    }

    @Test
    @DisplayName("답글에 좋아요 - 새로 생성")
    void testLikeReplyEmpty() {
        //given
        LikeReplyDto likeReplyDto = new LikeReplyDto(user, 1L, 1L, 2L, 1L);
        when(reply.getUser()).thenReturn(mock(User.class));
        when(reply.getUser().getId()).thenReturn(2L);
        when(replyRepository.findById(anyLong()))
                .thenReturn(Optional.of(reply));
        when(replyLikeRepository.findByUserIdAndReplyId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        //when
        replyService.likeReply(likeReplyDto);

        //then
        verify(replyLikeRepository, times(1)).save(any(ReplyLike.class));
        verify(notificationService, times(1)).send(likeReplyDto);
    }

}