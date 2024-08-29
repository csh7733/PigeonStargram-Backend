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
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.FileUploadWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_HASH;
import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_SET;
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

    private final RedisService redisService;
    private final FileUploadWorker fileUploadWorker;

    public List<ResponsePostDto> getPostsByUserId(Long userId) {
        return postCrudService.findPostIdsByUserId(userId).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedPost)
                .collect(Collectors.toList());
    }


    public List<ResponsePostDto> getRecentPostsByUser(Long userId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        return postCrudService.findPostIdsByUserIdAndCreatedDateAfter(userId, oneDayAgo).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                .map(this::getCombinedPost)
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

        // 캐시 히트된 경우 역직렬화된 데이터를 images로 복사
        List<Image> images = post.getImages();
        List<Image> imagesForSerialization = post.getImagesForSerialization();
        if(images.isEmpty() && !imagesForSerialization.isEmpty()){
            images.addAll(imagesForSerialization);
        }

        return buildPostContentDto(post);
    }

    public PostLikeDto getPostsLike(Long postId) {
        Integer count = postLikeCrudService.countByPostId(postId);
        return buildPostLikeDto(false, count);
    }

    public Long createPost(CreatePostDto dto) {
        User loginUser = userService.findById(dto.getLoginUserId());

        Post post = buildPost(dto, loginUser);
        Post save = postCrudService.save(post);
        List<Image> postImages = save.getImagesForSerialization();

        // 이미지 저장
        if (dto.getHasImage()) {
            dto.getImageUrls().stream()
                    .map(imageUrl -> {
                        Image image = Image.builder()
                                .img(imageUrl)
                                .featured(true)
                                .post(save)
                                .build();
                        postImages.add(image);
                        return image;
                    })
                    .forEach(imageRepository::save);

            // 이미지 업로드 중인 게시물 정보를 Redis Hash에 추가
            // UPLOADING_POSTS_HASH는 현재 이미지가 업로드 중인 게시물 정보를 저장하는 Redis Key
            // FieldKey : 게시물 ID를 위한 게시물 생성전 발급된 UUID , value : 게시물의 실제 ID
            log.info("Redis에서 업로드 기록 추가 - {}", dto.getFieldKey());
            redisService.putValueInHash(UPLOADING_POSTS_HASH, dto.getFieldKey(), save.getId());

            //이미지 업로드가 끝나지 않았을 경우
            if(!fileUploadWorker.isUploadComplete(dto.getFieldKey())) {
                // 업로드 중인 게시물 ID를 Set에 추가
                redisService.addToSet(UPLOADING_POSTS_SET, save.getId());
            }
        }
        postCrudService.updateImage(save);

        // 팔로우중인 유저에게 알림
        notifyFollowers(dto);
        // 태그된 유저에게 알림
        notifyTaggedUsers(dto, loginUser);

        return save.getId();
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
        postCrudService.edit(dto.getPostId(), dto.getContent());
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
