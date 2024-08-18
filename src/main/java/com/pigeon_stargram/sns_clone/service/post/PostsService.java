package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;


import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostsContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsLikeDto;
import com.pigeon_stargram.sns_clone.exception.post.PostsNotFoundException;
import com.pigeon_stargram.sns_clone.repository.post.PostsLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostsService {

    private final CommentService commentService;
    private final FollowService followService;
    private final NotificationService notificationService;

    private final PostsRepository postsRepository;
    private final PostsLikeRepository postsLikeRepository;

    public Posts getPostEntity(Long postId) {
        return postsRepository.findById(postId)
                .orElseThrow(() -> new PostsNotFoundException(POSTS_NOT_FOUND_ID));
    }

    public List<ResponsePostsDto> getPostsByUser(Long userId) {
        return postsRepository.findByUserId(userId).stream()
                .map(Posts::getId)
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedPost)
                .toList();
    }

    public ResponsePostsDto getCombinedPost(Long postId) {
        PostsContentDto postContentDto = getPostContent(postId);
        PostsLikeDto postsLikeDto = getPostsLike(postId);
        List<ResponseCommentDto> commentDtos = commentService.getCommentsByPostId(postId);
        return new ResponsePostsDto(postContentDto, postsLikeDto, commentDtos);
    }

    public PostsContentDto getPostContent(Long postId) {
        return new PostsContentDto(getPostEntity(postId));
    }

    public PostsLikeDto getPostsLike(Long postId) {
        Integer count = postsLikeRepository.countByPostId(postId);
        return new PostsLikeDto(false, count);
    }

    public ResponsePostsDto getPostById(Long postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));

        List<ResponseCommentDto> comments = commentService.getCommentListByPost(post.getId());
        Integer likeCount = postsLikeRepository.countByPostId(postId);

        return new ResponsePostsDto(post, comments, likeCount);
    }

    public Posts createPost(CreatePostDto dto) {
        Posts post = new Posts(dto.getUser(), dto.getContent());

        List<Long> notificationRecipientIds = followService.findFollows(dto.getUser().getId());
        dto.setNotificationRecipientIds(notificationRecipientIds);

        notificationService.save(dto);
        return postsRepository.save(post);
    }

    public Posts createPost(User user, String content, List<Image> images) {
        Posts post = new Posts(user,content,images);
        return postsRepository.save(post);
    }

    public void editPost(Long postId, String content) {
        Posts post = getPostEntity(postId);
        post.modify(content);
    }

    public void editPost(Long postId, String content, List<Image> images) {
        Posts post = getPostEntity(postId);
        post.modify(content,images);
    }

    public void deletePost(Long postId) {
        commentService.deleteAllCommentsAndReplyByPostId(postId);
        postsRepository.deleteById(postId);
    }

    public void likePost(LikePostDto dto) {
        Posts post = getPostEntity(dto.getPostId());
        User user = dto.getUser();

        postsLikeRepository.findByUserIdAndPostId(user.getId(), post.getId())
                .ifPresentOrElse(
                        existingLike -> {
                            postsLikeRepository.delete(existingLike);
                        },
                        () -> {
                            PostsLike postsLike = new PostsLike(user, post);
                            postsLikeRepository.save(postsLike);
                            notificationService.save(dto);
                        }
                );
    }

    private List<Long> getPostIdListByPosts(List<Posts> posts) {
        List<Long> postIds = posts.stream()
                .map(Posts::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        return postIds;
    }

    public List<ResponsePostsDto> getAllPosts() {
        List<Posts> posts = postsRepository.findAll();
        return posts.stream()
                .sorted(Comparator.comparing(Posts::getId).reversed())
                .map(post -> {
                    List<ResponseCommentDto> comments = commentService.getCommentListByPost(post.getId());
                    Integer likeCount = postsLikeRepository.countByPostId(post.getId());
                    return new ResponsePostsDto(post, comments, likeCount);
                })
                .collect(Collectors.toList());
    }


}
