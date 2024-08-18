package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostsContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsLikeDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.exception.post.PostsNotFoundException;
import com.pigeon_stargram.sns_clone.repository.comment.CommentRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
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
class PostsServiceTest {

    @Spy
    @InjectMocks
    private PostsService postsService;

    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;
    @Mock
    private FollowService followService;
    @Mock
    private NotificationService notificationService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostsRepository postsRepository;
    @Mock
    private PostsLikeRepository postsLikeRepository;

    private User user;
    private Posts post;
    private PostsLike postsLike;

    private List<Posts> posts = new ArrayList<>();
    private List<ResponsePostsDto> postsDtos = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        user = mock(User.class);
        post = mock(Posts.class);
        postsLike = mock(PostsLike.class);

        for (int i = 0; i < 3; i++) {
            posts.add(mock(Posts.class));
            postsDtos.add(mock(ResponsePostsDto.class));

            comments.add(mock(Comment.class));
        }
    }

    @Test
    @DisplayName("포스트 id로 엔티티 조회 - 성공")
    public void testGetPostEntitySuccess() {
        //given
        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));


        //when
        Posts postEntity = postsService.getPostEntity(1L);

        //then
        assertThat(postEntity).isEqualTo(post);
    }

    @Test
    @DisplayName("포스트 id로 엔티티 조회 - 포스트를 찾지 못함")
    public void testGetPostEntityPostNotFound() {
        //given
        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            postsService.getPostEntity(1L);
        }).isInstanceOf(PostsNotFoundException.class);
    }

    @Test
    @DisplayName("")
    public void testGetPostsByUser() {
        // Given
        when(posts.get(0).getId()).thenReturn(2L);
        when(posts.get(1).getId()).thenReturn(1L);
        when(posts.get(2).getId()).thenReturn(3L);
        when(postsRepository.findByUserId(anyLong())).thenReturn(posts);

        doReturn(new ResponsePostsDto()).when(postsService).getCombinedPost(1L);
        doReturn(new ResponsePostsDto()).when(postsService).getCombinedPost(2L);
        doReturn(new ResponsePostsDto()).when(postsService).getCombinedPost(3L);

        // When
        List<ResponsePostsDto> result = postsService.getPostsByUser(1L);

        // Then
        assertThat(result.size()).isEqualTo(3);
        verify(postsService, times(1)).getCombinedPost(1L);
        verify(postsService, times(1)).getCombinedPost(2L);
        verify(postsService, times(1)).getCombinedPost(3L);

    }

    @Test
    @DisplayName("포스트 id로 PostContent, PostLike, Comment 가져오기")
    public void testGetCombinedPost() {
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

        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        //postLike
        when(postsLikeRepository.countByPostId(anyLong()))
                .thenReturn(5);

        //comment
        when(commentService.getCommentsByPostId(anyLong()))
                .thenReturn(List.of());

        //when
        ResponsePostsDto combinedPostDto = postsService.getCombinedPost(1L);

        //then
        assertThat(combinedPostDto.getId()).isEqualTo(post.getId());
        assertThat(combinedPostDto.getProfile().getName())
                .isEqualTo(post.getUser().getName());
        assertThat(combinedPostDto.getData().getContent())
                .isEqualTo(post.getContent());
    }

    @Test
    @DisplayName("PostContent 가져오기")
    public void testGetPostContent() {
        //given
        when(post.getId()).thenReturn(1L);
        when(post.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(post.getModifiedDate()).thenReturn(LocalDateTime.of(2000, 1, 1, 0, 0));
        when(post.getContent()).thenReturn("content");
        when(post.getImages())
                .thenReturn(List.of(new Image("img-1", true),
                        new Image("img-2", false)));

        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));

        //when
        PostsContentDto postContent = postsService.getPostContent(1L);

        //then
        assertThat(postContent.getContent()).isEqualTo(post.getContent());
        assertThat(postContent.getId()).isEqualTo(post.getId());
        assertThat(postContent.getProfile().getId()).isEqualTo(post.getUser().getId());
        assertThat(postContent.getImages().getFirst().getImg())
                .isEqualTo(post.getImages().getFirst().getImg());
    }

    @Test
    @DisplayName("포스트 좋아요 가져오기")
    public void testGetPostsLike() {
        //given
        when(postsLikeRepository.countByPostId(anyLong()))
                .thenReturn(5);

        //when
        PostsLikeDto postsLikeDto = postsService.getPostsLike(1L);

        //then
        assertThat(postsLikeDto.getValue()).isEqualTo(5);
    }


    @Test
    @DisplayName("포스트 생성")
    public void testCreatePost() {
        //given
        CreatePostDto createPostDto = new CreatePostDto(user, "content");
        List<Long> recipientIds = List.of(1L, 2L, 3L);
        when(followService.findFollows(anyLong()))
                .thenReturn(recipientIds);
        when(notificationService.save(createPostDto))
                .thenReturn(List.of());
        when(postsRepository.save(any(Posts.class))).thenReturn(post);

        //when
        Posts createPost = postsService.createPost(createPostDto);

        //then
        assertThat(createPostDto.getNotificationRecipientIds())
                .isEqualTo(recipientIds);
        assertThat(createPost).isEqualTo(post);
    }


    @Test
    @DisplayName("포스트 수정")
    public void testEditPost() {
        //given
        Posts editPost = new Posts(user, "old-content");
        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(editPost));

        //when
        postsService.editPost(1L, "new-content");

        //then
        assertThat(editPost.getContent()).isEqualTo("new-content");
    }

    @Test
    @DisplayName("포스트 삭제")
    public void testDeletePost() {
        //given
        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        doNothing().when(commentService)
                .deleteAllCommentsAndReplyByPostId(anyLong());

        //when
        postsService.deletePost(1L);

        //then
        verify(postsRepository, times(1)).delete(post);
    }

    @Test
    @DisplayName("포스트에 좋아요 - 기존에 존재시 삭제")
    public void testLikePostExist() {
        //given
        LikePostDto likePostDto = new LikePostDto(user, 1L, 1L);

        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(postsLikeRepository.findByUserIdAndPostId(anyLong(), anyLong()))
                .thenReturn(Optional.of(postsLike));

        //when
        postsService.likePost(likePostDto);

        //then
        verify(postsLikeRepository, times(1)).delete(postsLike);
    }

    @Test
    @DisplayName("포스트에 좋아요 - 새로 생성")
    public void testLikePostEmpty() {
        //given
        LikePostDto likePostDto = new LikePostDto(user, 1L, 1L);

        when(postsRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        when(postsLikeRepository.findByUserIdAndPostId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        //when
        postsService.likePost(likePostDto);

        //then
        verify(postsLikeRepository, times(1)).save(any(PostsLike.class));
        verify(notificationService, times(1)).save(likePostDto);
    }
}
