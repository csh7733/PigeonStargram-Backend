package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostLikeDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostRepository;
import com.pigeon_stargram.sns_clone.service.comment.implV2.CommentServiceV2;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.implV2.NotificationServiceV2;
import com.pigeon_stargram.sns_clone.service.post.implV2.PostServiceV2;
import com.pigeon_stargram.sns_clone.service.user.UserService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Spy
    @InjectMocks
    PostServiceV2 postService;

    @Mock
    UserService userService;
    @Mock
    CommentServiceV2 commentService;
    @Mock
    FollowService followService;
    @Mock
    NotificationServiceV2 notificationService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;

    User user;
    Post post;
    PostLike postLike;

    List<Post> posts = new ArrayList<>();
    List<ResponsePostDto> postDtos = new ArrayList<>();

    List<Comment> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        post = mock(Post.class);
        postLike = mock(PostLike.class);

        for (int i = 0; i < 3; i++) {
            posts.add(mock(Post.class));
            postDtos.add(mock(ResponsePostDto.class));

            comments.add(mock(Comment.class));
        }
    }

    @Test
    @DisplayName("포스트 id로 엔티티 조회 - 성공")
    void testGetPostEntitySuccess() {
        //given
        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        //when
        Post postEntity = postService.findById(1L);

        //then
        assertThat(postEntity).isEqualTo(post);
    }

    @Test
    @DisplayName("포스트 id로 엔티티 조회 - 포스트를 찾지 못함")
    void testGetPostEntityPostNotFound() {
        //given
        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            postService.findById(1L);
        }).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("유저 id로 모든 포스트 가져오기")
    void testGetPostsByUser() {
        // Given
        when(posts.get(0).getId()).thenReturn(2L);
        when(posts.get(1).getId()).thenReturn(1L);
        when(posts.get(2).getId()).thenReturn(3L);
        when(postRepository.findByUserId(anyLong())).thenReturn(posts);

        doReturn(new ResponsePostDto()).when(postService).getCombinedPost(1L);
        doReturn(new ResponsePostDto()).when(postService).getCombinedPost(2L);
        doReturn(new ResponsePostDto()).when(postService).getCombinedPost(3L);

        // When
        List<ResponsePostDto> result = postService.getPostsByUserId(1L);

        // Then
        assertThat(result.size()).isEqualTo(3);
        verify(postService, times(1)).getCombinedPost(1L);
        verify(postService, times(1)).getCombinedPost(2L);
        verify(postService, times(1)).getCombinedPost(3L);

    }

    @Test
    @DisplayName("포스트 id로 PostContent, PostLike, Comment 가져오기")
    void testGetCombinedPost() {
        //given
        //postContent
        when(post.getId()).thenReturn(1L);
        when(post.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(post.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(post.getContent()).thenReturn("content");
        when(post.getImages())
                .thenReturn(List.of(new Image("img-1", true),
                        new Image("img-2", false)));

        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        //postLike
        when(postLikeRepository.countByPostId(anyLong()))
                .thenReturn(5);

        //comment
        when(commentService.getCommentResponseByPostIdAndLastCommentId(anyLong()))
                .thenReturn(List.of());

        //when
        ResponsePostDto combinedPostDto = postService.getCombinedPost(1L);

        //then
        assertThat(combinedPostDto.getId()).isEqualTo(post.getId());
        assertThat(combinedPostDto.getProfile().getName())
                .isEqualTo(post.getUser().getName());
        assertThat(combinedPostDto.getData().getContent())
                .isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("PostContent 가져오기")
    void testGetPostContent() {
        //given
        when(post.getId()).thenReturn(1L);
        when(post.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(post.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(post.getContent()).thenReturn("content");
        when(post.getImages())
                .thenReturn(List.of(new Image("img-1", true),
                        new Image("img-2", false)));

        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        //when
        PostContentDto postContent = postService.getPostContent(1L);

        //then
        assertThat(postContent.getContent()).isEqualTo(post.getContent());
        assertThat(postContent.getId()).isEqualTo(post.getId());
        assertThat(postContent.getProfile().getId()).isEqualTo(post.getUser().getId());
        assertThat(postContent.getImages().getFirst().getImg())
                .isEqualTo(post.getImages().getFirst().getImg());
    }

    @Test
    @DisplayName("포스트 좋아요 가져오기")
    void testGetPostsLike() {
        //given
        when(postLikeRepository.countByPostId(anyLong()))
                .thenReturn(5);

        //when
        PostLikeDto postsLikeDto = postService.getPostsLike(1L);

        //then
        assertThat(postsLikeDto.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("포스트 생성")
    void testCreatePost() {
        //given
        CreatePostDto createPostDto = new CreatePostDto(user, "content");
        List<Long> recipientIds = List.of(1L, 2L, 3L);
        when(followService.findNotificationEnabledFollowerIds(anyLong()))
                .thenReturn(recipientIds);
        when(notificationService.sendToSplitWorker(createPostDto))
                .thenReturn(List.of());
        when(postRepository.save(any(Post.class))).thenReturn(post);

        //when
        Post createPost = postService.createPost(createPostDto);

        //then
        assertThat(createPostDto.getNotificationRecipientIds())
                .isEqualTo(recipientIds);
        assertThat(createPost).isEqualTo(post);
    }


    @Test
    @DisplayName("포스트 수정")
    void testEditPost() {
        //given
        Post editPost = new Post(user, "old-content");
        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(editPost));

        //when
        postService.editPost(1L, "new-content");

        //then
        assertThat(editPost.getContent()).isEqualTo("new-content");
    }

    @Test
    @DisplayName("포스트 삭제")
    void testDeletePost() {
        //given
        doNothing().when(commentService)
                .deleteAllCommentsAndReplyByPostId(anyLong());

        //when
        postService.deletePost(1L);

        //then
        verify(commentService, times(1)).deleteAllCommentsAndReplyByPostId(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("포스트에 좋아요 - 기존에 존재시 삭제")
    void testLikePostExist() {
        //given
        LikePostDto likePostDto = new LikePostDto(user, 1L, 1L);

        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserIdAndPostId(anyLong(), anyLong()))
                .thenReturn(Optional.of(postLike));

        //when
        postService.likePost(likePostDto);

        //then
        verify(postLikeRepository, times(1)).delete(postLike);
    }

    @Test
    @DisplayName("포스트에 좋아요 - 새로 생성")
    void testLikePostEmpty() {
        //given
        LikePostDto likePostDto = new LikePostDto(user, 1L, 1L);

        when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserIdAndPostId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        //when
        postService.likePost(likePostDto);

        //then
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
        verify(notificationService, times(1)).sendToSplitWorker(likePostDto);
    }
}
