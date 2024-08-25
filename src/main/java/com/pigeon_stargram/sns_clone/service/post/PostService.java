package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;


import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostLikeDto;
import com.pigeon_stargram.sns_clone.repository.post.ImageRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostCrudService postCrudService;
    private final PostLikeCrudService postLikeCrudService;
    private final CommentService commentService;
    private final FollowService followService;
    private final NotificationService notificationService;

    private final ImageRepository imageRepository;
    private final UserService userService;

    public List<ResponsePostDto> getPostsByUserId(Long userId) {
        return postCrudService.findByUserId(userId).stream()
                .map(Post::getId)
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedPost)
                .collect(Collectors.toList());
    }

    public List<ResponsePostDto> getRecentPostsByUser(Long userId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        return postCrudService.findByUserIdAndCreatedDateAfter(userId, oneDayAgo).stream()
                .map(post -> getCombinedPost(post.getId()))
                .collect(Collectors.toList());
    }

    public ResponsePostDto getCombinedPost(Long postId) {
        PostContentDto contentDto = getPostContent(postId);
        PostLikeDto likeDto = getPostsLike(postId);
        List<ResponseCommentDto> commentDtos = commentService.getCommentDtosByPostId(postId);
        return buildResponsePostDto(contentDto, likeDto, commentDtos);
    }

    public PostContentDto getPostContent(Long postId) {
        Post post = postCrudService.findById(postId);
        return buildPostContentDto(post);
    }

    public PostLikeDto getPostsLike(Long postId) {
        Integer count = postLikeCrudService.countByPostId(postId);
        return buildPostLikeDto(false, count);
    }

    public Post createPost(CreatePostDto dto) {
        User loginUser = userService.findById(dto.getLoginUserId());

        Post post = buildPost(dto, loginUser);
        Post save = postCrudService.save(post);

        // 이미지 저장
        if (dto.getHasImage()) {
            dto.getImageUrls().stream()
                    .map(imageUrl -> Image.builder()
                            .img(imageUrl)
                            .featured(true)
                            .post(post)
                            .build())
                    .forEach(imageRepository::save);
        }

        // 팔로우중인 유저에게 알림
        notifyFollowers(dto);
        // 태그된 유저에게 알림
        notifyTaggedUsers(dto, loginUser);

        return save;
    }

    private void notifyFollowers(CreatePostDto dto) {
        List<Long> notificationRecipientIds = followService.findFollows(dto.getLoginUserId());
        dto.setNotificationRecipientIds(notificationRecipientIds);

        notificationService.send(dto);
    }

    private void notifyTaggedUsers(CreatePostDto dto, User loginUser) {
        NotifyPostTaggedDto notifyPostTaggedDto =
                buildNotifyPostTaggedDto(dto, loginUser);
        notificationService.notifyTaggedUsers(notifyPostTaggedDto);
    }

    public void editPost(EditPostDto dto) {
        Post post = postCrudService.findById(dto.getPostId());
        post.modify(dto.getContent());
    }


    public void deletePost(Long postId) {
        commentService.deleteAllCommentsAndReplyByPostId(postId);
        postCrudService.deleteById(postId);
    }

    public void likePost(LikePostDto dto) {
        User user = userService.findById(dto.getLoginUserId());
        Post post = postCrudService.findById(dto.getPostId());

        postLikeCrudService.findByUserIdAndPostId(user.getId(), post.getId())
                .ifPresentOrElse(
                        existingLike -> {
                            postLikeCrudService.delete(existingLike);
                        },
                        () -> {
                            PostLike postsLike = buildPostLike(user, post);
                            postLikeCrudService.save(postsLike);
                            notificationService.send(dto);
                        }
                );
    }

}
