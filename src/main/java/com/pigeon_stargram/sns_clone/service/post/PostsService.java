package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;

import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.CommentDto;
import com.pigeon_stargram.sns_clone.dto.post.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.repository.post.PostsLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final PostsLikeRepository postsLikeRepository;
    private final CommentService commentService;
    private final FollowService followService;
    private final NotificationService notificationService;

    public Posts createPost(CreatePostDto dto) {
        Posts post = new Posts(dto.getUser(), dto.getContent());

        List<Follow> follows = followService.findFollows(dto.getUser().getId());
        List<Long> notificationRecipientIds = followService.findFollows(dto.getUser().getId()).stream()
                .filter(Follow::getIsNotificationEnabled)
                .map(follow -> follow.getRecipient().getId())
                .toList();
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
        Posts post = getPostEntity(postId);

        commentService.deleteAllCommentsAndReplyByPostId(postId);

        postsRepository.delete(post);
    }

    public void likePost(LikePostDto dto) {
        Posts post = getPostEntity(dto.getPostId());
        dto.setWriterId(post.getUser().getId());

        postsLikeRepository.findByUserAndPost(dto.getUser(), post)
                .ifPresentOrElse(
                        existingLike -> {
                            postsLikeRepository.delete(existingLike);
                            post.decrementLikes();
                        },
                        () -> {
                            PostsLike postsLike = new PostsLike(dto.getUser(), post);
                            postsLikeRepository.save(postsLike);
                            post.incrementLikes();
                            notificationService.save(dto);
                        }
                );
    }

    public List<PostsDto> getPostsByUser(User user) {
        List<Posts> posts = postsRepository.findByUserId(user.getId());

        List<Long> postIds = getPostIdListByUser(posts);

        return postIds.stream()
                .map(this::getPostById)
                .collect(Collectors.toList());
    }

    private static List<Long> getPostIdListByUser(List<Posts> posts) {
        List<Long> postIds = posts.stream()
                .map(Posts::getId)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        return postIds;
    }


    public List<PostsDto> getAllPosts() {
        List<Posts> posts = postsRepository.findAll();
        return posts.stream()
                .sorted(Comparator.comparing(Posts::getId).reversed())
                .map(post -> {
                    List<CommentDto> comments = commentService.getCommentListByPost(post.getId());
                    return new PostsDto(post, comments);
                })
                .collect(Collectors.toList());
    }

    public PostsDto getPostById(Long postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));

        List<CommentDto> comments = commentService.getCommentListByPost(post.getId());

        return new PostsDto(post, comments);
    }


    public Posts getPostEntity(Long postId) {
        return postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }
}
