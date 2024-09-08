package com.pigeon_stargram.sns_clone.service.comment;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CommentContentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.internal.LikeCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentLikeDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.exception.comment.CommentNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentLikeRepository;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.when;

@Disabled("All tests in this class are disabled")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Spy
    @InjectMocks
    CommentService commentService;

    @Mock
    ReplyService replyService;
    @Mock
    NotificationService notificationService;

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentLikeRepository commentLikeRepository;

    User user;
    Post post;
    Comment comment;
    CommentLike commentLike;

    List<Comment> comments = new ArrayList<>();
    List<ResponseCommentDto> commentDtos = new ArrayList<>();

    List<Reply> replies = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        post = mock(Post.class);
        comment = mock(Comment.class);
        commentLike = mock(CommentLike.class);

        for(int i = 0; i < 3; i++){
            comments.add(mock(Comment.class));
            commentDtos.add(mock(ResponseCommentDto.class));

            replies.add(mock(Reply.class));
        }
    }

    @Test
    @DisplayName("댓글 id로 엔티티 조회 - 성공")
    void testGetCommentEntitySuccess() {
        //given
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));

        //when
        Comment commentEntity = commentService.findById(1L);

        //then
        assertThat(commentEntity).isEqualTo(comment);
    }

    @Test
    @DisplayName("댓글 id로 엔티티 조회 - 댓글를 찾지 못함")
    void testGetCommentEntityPostNotFound() {
        //given
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            commentService.findById(1L);
        }).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("포스트 id로 모든 댓글 가져오기")
    void testGetCommentByUser() {
        // Given
        when(comments.get(0).getId()).thenReturn(2L);
        when(comments.get(1).getId()).thenReturn(1L);
        when(comments.get(2).getId()).thenReturn(3L);
        when(commentRepository.findByPostId(anyLong())).thenReturn(comments);

        doReturn(new ResponseCommentDto()).when(commentService).getCombinedComment(1L);
        doReturn(new ResponseCommentDto()).when(commentService).getCombinedComment(2L);
        doReturn(new ResponseCommentDto()).when(commentService).getCombinedComment(3L);

        // When
        List<ResponseCommentDto> result = commentService.getCommentResponseByPostIdAndLastCommentId(1L);

        // Then
        assertThat(result.size()).isEqualTo(3);
        verify(commentService, times(1)).getCombinedComment(1L);
        verify(commentService, times(1)).getCombinedComment(2L);
        verify(commentService, times(1)).getCombinedComment(3L);

    }
    
    @Test
    @DisplayName("댓글 id로 CommentContent, CommentLike, Reply 가져오기")
    void testGetCombinedComment() {
        //given
        //commentContent
        when(comment.getId()).thenReturn(1L);
        when(comment.getUser()).thenReturn(user);
        when(comment.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(comment.getContent()).thenReturn("content");

        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));

        //commentLike
        when(commentLikeRepository.countByCommentId(anyLong()))
                .thenReturn(5);

        //reply
        when(replyService.getReplyDtosByCommentId(anyLong()))
                .thenReturn(List.of());

        //when
        ResponseCommentDto combinedCommentDto = commentService.getCombinedComment(1L);

        //then
        assertThat(combinedCommentDto.getId()).isEqualTo(comment.getId());
        assertThat(combinedCommentDto.getProfile().getName())
                .isEqualTo(comment.getUser().getName());
        assertThat(combinedCommentDto.getData().getComment())
                .isEqualTo(comment.getContent());
    }

    @Test
    @DisplayName("CommentContent 가져오기")
    void testGetCommentContent() {
        //given
        when(comment.getId()).thenReturn(1L);
        when(comment.getUser()).thenReturn(user);
        when(comment.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(comment.getContent()).thenReturn("content");

        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));

        //when
        CommentContentDto commentContent = commentService.getCommentContent(1L);

        //then
        assertThat(commentContent.getComment()).isEqualTo(comment.getContent());
        assertThat(commentContent.getId()).isEqualTo(comment.getId());
        assertThat(commentContent.getProfile().getId()).isEqualTo(comment.getUser().getId());
    }

    @Test
    @DisplayName("댓글 좋아요 가져오기")
    void testGetCommentLike() {
        //given
        when(commentLikeRepository.countByCommentId(anyLong()))
                .thenReturn(5);

        //when
        CommentLikeDto commentLikeDto = commentService.getCommentLike(1L);

        //then
        assertThat(commentLikeDto.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("댓글 생성")
    void testCreateComment() {
        //given
        CreateCommentDto createCommentDto = new CreateCommentDto(user, post, "content");

        when(post.getUser()).thenReturn(mock(User.class));
        when(post.getUser().getId()).thenReturn(1L);

        when(notificationService.sendToSplitWorker(createCommentDto))
                .thenReturn(List.of());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        //when
        Comment createComment = commentService.createComment(createCommentDto);

        //then
        assertThat(createCommentDto.toRecipientIds().getFirst())
                .isEqualTo(post.getUser().getId());
        assertThat(createComment).isEqualTo(comment);
    }

    @Test
    @DisplayName("댓글 수정")
    void testEditComment() {
        //given
        Comment editComment = new Comment(user, post, "old-content");
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(editComment));

        //when
        commentService.editComment(1L, "new-content");

        //then
        assertThat(editComment.getContent()).isEqualTo("new-content");
    }

    @Test
    @DisplayName("댓글 삭제")
    void testDeleteComment() {
        //given
        doNothing().when(replyService)
                .deleteAllRepliesByCommentId(anyLong());

        //when
        commentService.deleteById(1L);

        //then
        verify(replyService, times(1))
                .deleteAllRepliesByCommentId(1L);
        verify(commentRepository, times(1))
                .deleteById(1L);
    }

    @Test
    @DisplayName("댓글에 좋아요 - 기존에 존재시 삭제")
    void testLikeCommentExist() {
        //given
        LikeCommentDto likeCommentDto = new LikeCommentDto(user, 1L, 1L, 2L, 1L);
        when(comment.getUser()).thenReturn(mock(User.class));
        when(comment.getUser().getId()).thenReturn(2L);

        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(commentLike));

        //when
        commentService.likeComment(likeCommentDto);

        //then
        verify(commentLikeRepository, times(1))
                .delete(commentLike);
    }

    @Test
    @DisplayName("댓글에 좋아요 - 새로 생성")
    void testLikeCommentEmpty() {
        //given
        LikeCommentDto likeCommentDto = new LikeCommentDto(user, 1L, 1L, 2L, 1L);
        when(comment.getUser()).thenReturn(mock(User.class));
        when(comment.getUser().getId()).thenReturn(2L);
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByUserIdAndCommentId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        //when
        commentService.likeComment(likeCommentDto);

        //then
        verify(commentLikeRepository, times(1)).save(any(CommentLike.class));
        verify(notificationService, times(1)).sendToSplitWorker(likeCommentDto);
    }

}