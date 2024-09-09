package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import com.pigeon_stargram.sns_clone.dto.post.response.PostLikeDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.repository.post.ImageRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentCrudService;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
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

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_HASH;
import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_SET;
import static com.pigeon_stargram.sns_clone.service.post.PostBuilder.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTimeMillis;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostCrudService postCrudService;
    private final PostLikeCrudService postLikeCrudService;
    private final CommentService commentService;
    private final FollowService followService;
    private final FollowCrudService followCrudService;
    private final NotificationService notificationService;

    private final ImageRepository imageRepository;
    private final UserService userService;

    private final RedisService redisService;
    private final FileUploadWorker fileUploadWorker;
    private final CommentCrudService commentCrudService;

    public List<ResponsePostDto> getPostsByUserId(Long userId) {
            return postCrudService.findPostIdByUserId(userId).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedPost)
                .collect(Collectors.toList());
    }


    public List<ResponsePostDto> getRecentPostsByUser(Long userId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        // 캐시된 게시물 ID를 가져오고, 유효하지 않은 게시물(24시간 지난)을 필터링 후 캐시에서 제거
        List<Long> postIds = postCrudService.findPostIdsByUserIdAndCreatedDateAfter(userId, oneDayAgo).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))  // 업로드 중인 게시물 제외
                .filter(postId -> {
                    // 게시물 조회
                    Post post = postCrudService.findById(postId);

                    // 게시물이 24시간 내에 작성되었는지 확인
                    boolean isRecent = post.getCreatedDate().isAfter(oneDayAgo);

                    // 24시간 지난 게시물은 Redis 캐시에서 제거
                    if (!isRecent) {
                        String cacheKey = cacheKeyGenerator(RECENT_POST_IDS, USER_ID, userId.toString());
                        redisService.removeFromSet(cacheKey, postId);
                    }

                    // 유효한 게시물만 남김
                    return isRecent;
                })
                .collect(Collectors.toList());

        // 유효한 게시물로 ResponsePostDto 변환 후 반환
        return postIds.stream()
                .map(this::getCombinedPost)
                .collect(Collectors.toList());
    }

    public ResponsePostDto getCombinedPost(Long postId) {
        Long lastCommentId = 0L;

        PostContentDto contentDto = getPostContent(postId);
        PostLikeDto likeDto = getPostsLike(postId);

        List<ResponseCommentDto> commentDtos = commentService
                .getCommentResponseByPostIdAndLastCommentId(postId, lastCommentId);

        Boolean isMoreComments = commentCrudService.getIsMoreComment(postId, lastCommentId);

        return buildResponsePostDto(contentDto, likeDto, commentDtos, isMoreComments);
    }

    public ResponsePostDto getPostByPostId(Long postId) {
        // 아직 업로드중인 post는 리턴하지 않음
        if (redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId)) {
            return null;
        }

        // 그 외에는 포스트를 가져옴
        return getCombinedPost(postId);
    }


    public PostContentDto getPostContent(Long postId) {
        Post post = postCrudService.findById(postId);

        // 캐시 히트된 경우 역직렬화된 데이터를 images로 복사
        List<Image> images = post.getImages();
        List<Image> imagesForSerialization = post.getImagesForSerialization();
        if (images.isEmpty() && !imagesForSerialization.isEmpty()) {
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

        List<Long> notificationEnabledIds =
                followCrudService.findNotificationEnabledIds(loginUser.getId());
        dto.setNotificationRecipientIds(notificationEnabledIds);

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
            redisService.putValueInHash(UPLOADING_POSTS_HASH, dto.getFieldKey(), save.getId(),5 * ONE_MINUTE_TTL);

            //이미지 업로드가 끝나지 않았을 경우
            if (!fileUploadWorker.isUploadComplete(dto.getFieldKey())) {
                // 업로드 중인 게시물 ID를 Set에 추가
                redisService.addToSet(UPLOADING_POSTS_SET, save.getId(), 5 * ONE_MINUTE_TTL);
            }
        }
        postCrudService.updateImage(save);

        // 팔로우중인 유저에게 알림
        if (!notificationEnabledIds.isEmpty()) {
            dto.setLoginUserName(loginUser.getName());
            notifyFollowers(dto);
        }
        // 태그된 유저에게 알림
        notifyTaggedUsers(dto, loginUser);

        // 팔로워 타임라인에 게시물 추가
        if (!followService.isFamousUser(loginUser.getId())) {
            // 팔로워 목록 가져오기
            List<Long> followerIds = followService.getFollowerIds(loginUser.getId());

            // 현재 시간을 타임스탬프로 사용
            Double currentTime = getCurrentTimeMillis();

            // 각 팔로워의 타임라인에 게시물 추가
            followerIds.forEach(followerId -> {
                String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, followerId.toString());
                redisService.addToSortedSet(timelineKey, currentTime, save.getId());
            });
        }

        return save.getId();
    }

    private void notifyFollowers(CreatePostDto dto) {
        List<Long> notificationRecipientIds = followService.findFollows(dto.getLoginUserId());
        dto.setNotificationRecipientIds(notificationRecipientIds);

        notificationService.sendToSplitWorker(dto);
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

    public Boolean likePost(LikePostDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long postId = dto.getPostId();

        User loginUser = userService.findById(loginUserId);
        dto.setLoginUserName(loginUser.getName());
        postLikeCrudService.toggleLike(loginUserId, postId);

        // 좋아요수가 증가할때 알림 보내기
        List<Long> postLikeUserIds = postLikeCrudService.getPostLikeUserIds(postId);
        if (postLikeUserIds.contains(loginUserId)) {
            notificationService.sendToSplitWorker(dto);
            return true;
        }
        return false;
    }
}
