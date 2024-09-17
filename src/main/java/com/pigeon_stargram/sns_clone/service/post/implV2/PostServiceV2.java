package com.pigeon_stargram.sns_clone.service.post.implV2;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostFactory;
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
import com.pigeon_stargram.sns_clone.service.comment.implV2.CommentCrudServiceV2;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import com.pigeon_stargram.sns_clone.service.post.PostCrudService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import com.pigeon_stargram.sns_clone.worker.file.FileUploadWorker;
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
import static com.pigeon_stargram.sns_clone.domain.post.PostFactory.createImage;
import static com.pigeon_stargram.sns_clone.dto.post.PostDtoConverter.*;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTimeMillis;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;

// 게시물에 대한 캐싱을 적용한 PostService 구현체
// Value  | Structure | Key                  | FieldKey
// -----  | --------- | -------------------- | --------
// postId | Hash      | UPLOADING_POSTS_HASH | fieldKey  임시 postId로 실제 postId를 찾는 용도
// postId | Set       | UPLOADING_POSTS_SET  |           업로드중인 게시물
// postId | Set       | TIMELINE             |           유저에 대한 타임라인 게시물
@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceV2 implements PostService {

    private final PostCrudService postCrudService;
    private final PostLikeCrudServiceV2 postLikeCrudService;
    private final CommentService commentService;
    private final FollowService followService;
    private final FollowCrudService followCrudService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final RedisService redisService;
    private final CommentCrudServiceV2 commentCrudService;

    private final ImageRepository imageRepository;

    private final FileUploadWorker fileUploadWorker;

    @Transactional(readOnly = true)
    @Override
    public Post findById(Long postId) {
        return postCrudService.findById(postId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponsePostDto> getPostsByUserId(Long userId) {
        return postCrudService.findPostIdByUserId(userId).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId)) // 업로드 중인 게시물 제외
                .sorted(Comparator.reverseOrder()) // 최신순으로 정렬
                .map(this::getCombinedPost)         // 게시물 조회
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponsePostDto> getRecentPostsByUser(Long userId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        // 최근 게시물 ID 조회
        List<Long> recentPostIds = getRecentPostIds(userId, oneDayAgo);

        return recentPostIds.stream()
                .map(this::getCombinedPost)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ResponsePostDto getCombinedPost(Long postId) {
        PostContentDto contentDto = getPostContent(postId);
        PostLikeDto likeDto = getPostsLike(postId);

        // 처음 게시물 로딩시 댓글을 지정된 갯수만큼 불러옵니다.
        Long lastCommentId = 0L;    // 처음에는 댓글 ID가 없음
        // 댓글 조회
        List<ResponseCommentDto> commentDtos = commentService
                .getCommentResponseByPostIdAndLastCommentId(postId, lastCommentId);
        // 추가 댓글 여부 조회
        Boolean isMoreComments = commentCrudService.getIsMoreComments(postId, lastCommentId);

        return toResponsePostDto(contentDto, likeDto, commentDtos, isMoreComments);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponsePostDto getPostByPostId(Long postId) {
        if (redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId)) {
            // 업로드 중인 게시물은 반환하지 않음
            return null;
        }
        return getCombinedPost(postId);
    }

    @Transactional(readOnly = true)
    @Override
    public PostContentDto getPostContent(Long postId) {
        Post post = postCrudService.findById(postId);

        // 캐시된 직렬화전용 이미지를 연관관계의 이미지로 복사
        copyCachedImage(post);

        return toPostContentDto(post);
    }

    @Transactional(readOnly = true)
    @Override
    public PostLikeDto getPostsLike(Long postId) {
        Integer count = postLikeCrudService.countByPostId(postId);
        return toPostLikeDto(false, count);
    }

    @Transactional
    @Override
    public Long createPost(CreatePostDto dto) {
        User loginUser = userService.getUserById(dto.getLoginUserId());
        Post post = PostFactory.createPost(dto, loginUser);

        // 게시물 저장
        Post savedPost = postCrudService.save(post);

        // 이미지 처리
        processPostImageUpload(dto, savedPost);
        // 알림 처리
        notifyFollowersAndTaggedUsers(dto, loginUser);
        // 타임라인 처리
        addPostToFollowersTimeline(loginUser, savedPost);

        return savedPost.getId();
    }

    @Transactional
    @Override
    public void editPost(EditPostDto dto) {
        postCrudService.edit(dto.getPostId(), dto.getContent());
    }

    @Transactional
    @Override
    public void deletePost(Long postId) {
        // 댓글 및 답글 삭제
        commentService.deleteAllCommentsAndReplyByPostId(postId);

        // 게시물 삭제
        postCrudService.deleteById(postId);
    }

    @Transactional
    @Override
    public Boolean likePost(LikePostDto dto) {
        Long loginUserId = dto.getLoginUserId();
        Long postId = dto.getPostId();

        User loginUser = userService.getUserById(loginUserId);
        dto.setLoginUserName(loginUser.getName());
        // 좋아요 상태 토글
        postLikeCrudService.toggleLike(loginUserId, postId);

        // 좋아요수가 증가할때 알림 보내고 true를 반환
        List<Long> postLikeUserIds = postLikeCrudService.getPostLikeUserIds(postId);
        if (postLikeUserIds.contains(loginUserId)) {
            notificationService.sendToSplitWorker(dto);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Boolean existsById(Long postId){
        return postCrudService.existsById(postId);
    }

    /**
     * 주어진 사용자 ID로 최근 게시물 ID 목록을 조회합니다.
     * 게시물이 최근 24시간 이내 작성된 경우만 포함됩니다.
     *
     * @param userId    사용자 ID
     * @param oneDayAgo 24시간 전 시간
     * @return 최근 게시물 ID 리스트
     */
    private List<Long> getRecentPostIds(Long userId, LocalDateTime oneDayAgo) {
        // 캐시된 게시물 ID를 가져오고, 유효하지 않은 게시물(24시간 지난)을 필터링 후 캐시에서 제거
        return postCrudService.findPostIdsByUserIdAndCreatedDateAfter(userId, oneDayAgo).stream()
                .filter(postId -> !redisService.isMemberOfSet(UPLOADING_POSTS_SET, postId))  // 업로드 중인 게시물 제외
                .filter(postId -> isRecentPost(userId, postId, oneDayAgo))  // 최근 게시물 필터링
                .collect(Collectors.toList());
    }

    /**
     * 게시물이 최근 24시간 이내 작성된 경우 유효한 게시물로 판별합니다.
     *
     * @param userId    사용자 ID
     * @param postId    게시물 ID
     * @param oneDayAgo 24시간 전 시간
     * @return 게시물 유효 여부
     */
    private boolean isRecentPost(Long userId,
                                 Long postId,
                                 LocalDateTime oneDayAgo) {
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
    }

    /**
     * 게시물에 포함된 이미지들을 처리합니다.
     * 캐시된 이미지를 복사하여 게시물 이미지로 만듭니다.
     *
     * @param post 게시물 엔티티
     */
    private void copyCachedImage(Post post) {
        // 캐시 히트된 경우 역직렬화된 데이터를 images 필드로 복사
        List<Image> images = post.getImages();
        List<Image> imagesForSerialization = post.getImagesForSerialization();
        if (images.isEmpty() && !imagesForSerialization.isEmpty()) {
            images.addAll(imagesForSerialization);
        }
    }

    /**
     * 게시물 이미지와 관련된 처리를 수행합니다.
     * 업로드된 이미지가 있으면 이를 게시물에 포함시킵니다.
     *
     * @param dto       게시물 생성 DTO
     * @param savedPost 저장된 게시물
     */
    private void processPostImageUpload(CreatePostDto dto,
                                        Post savedPost) {
        List<Image> postImages = savedPost.getImagesForSerialization();
        // 이미지 저장
        if (dto.getHasImage()) {
            saveImage(dto, savedPost, postImages);

            // 이미지 업로드 중인 게시물 정보를 Redis Hash에 추가
            // UPLOADING_POSTS_HASH는 현재 이미지가 업로드 중인 게시물 정보를 저장하는 Redis Key
            // FieldKey : 게시물 ID를 위한 게시물 생성전 발급된 UUID , value : 게시물의 실제 ID
            redisService.putValueInHash(UPLOADING_POSTS_HASH, dto.getFieldKey(), savedPost.getId(), 5 * ONE_MINUTE_TTL);

            //이미지 업로드가 끝나지 않았을 경우
            if (!fileUploadWorker.isUploadComplete(dto.getFieldKey())) {
                // 업로드 중인 게시물 ID를 Set에 추가
                redisService.addToSet(UPLOADING_POSTS_SET, savedPost.getId(), 5 * ONE_MINUTE_TTL);
            }
        }
        postCrudService.updateImage(savedPost);
    }

    /**
     * 게시물 생성 시 이미지 URL 목록을 처리하고, 이미지를 데이터베이스에 저장합니다.
     *
     * @param dto        게시물 생성 DTO
     * @param savedPost  저장된 게시물 엔티티
     * @param postImages 게시물과 연결된 이미지 목록
     */
    private void saveImage(CreatePostDto dto,
                           Post savedPost,
                           List<Image> postImages) {
        dto.getImageUrls().stream()
                .map(imageUrl -> {
                    // 이미지 URL을 기반으로 Image 객체를 생성
                    Image image = createImage(imageUrl, savedPost);
                    postImages.add(image);
                    return image;
                })
                .forEach(imageRepository::save); // 각 이미지를 저장
    }

    /**
     * 게시물 작성 시 알림을 처리합니다.
     * 팔로워 및 태그된 유저들에게 알림을 전송합니다.
     *
     * @param dto       게시물 생성 DTO
     * @param loginUser 게시물 작성자
     */
    private void notifyFollowersAndTaggedUsers(CreatePostDto dto,
                                               User loginUser) {
        // dto에 알림 수신자 목록 추가
        List<Long> notificationEnabledIds =
                followCrudService.findNotificationEnabledIds(loginUser.getId());
        dto.setNotificationRecipientIds(notificationEnabledIds);

        if (!notificationEnabledIds.isEmpty()) {
            dto.setLoginUserName(loginUser.getName());
            // 팔로워에게 알림 전송
            notifyFollowers(dto);
        }

        // 태그된 유저에게 알림 전송
        notifyTaggedUsers(dto, loginUser);
    }

    /**
     * 게시물 작성자의 팔로워들에게 알림을 전송합니다.
     *
     * @param dto 게시물 생성 DTO
     */
    private void notifyFollowers(CreatePostDto dto) {
        // 알림 수신 설정이 되어 있는 팔로워 ID 목록을 조회
        List<Long> recipientIds =
                followService.findNotificationEnabledFollowerIds(dto.getLoginUserId());
        dto.setNotificationRecipientIds(recipientIds);

        // 팔로워들에게 알림을 전송
        notificationService.sendToSplitWorker(dto);
    }

    /**
     * 게시물에 태그된 유저들에게 알림을 전송합니다.
     *
     * @param dto       게시물 생성 DTO
     * @param loginUser 게시물 작성자
     */
    private void notifyTaggedUsers(CreatePostDto dto, User loginUser) {
        NotifyPostTaggedDto notifyPostTaggedDto =
                toNotifyPostTaggedDto(dto, loginUser);
        // 태그된 유저들에게 알림을 전송
        notificationService.notifyTaggedUsers(notifyPostTaggedDto);
    }

    /**
     * 게시물 작성자의 팔로워들에게 게시물을 타임라인에 추가합니다.
     * 팔로워가 많지 않은 경우에만 처리됩니다.
     *
     * @param loginUser 게시물 작성자
     * @param savedPost 저장된 게시물 엔티티
     */
    private void addPostToFollowersTimeline(User loginUser,
                                            Post savedPost) {
        // 팔로워 수가 많지 않은 경우 팔로워 타임라인에 게시물 추가
        if (!followService.isFamousUser(loginUser.getId())) {
            // 팔로워 목록 가져오기
            List<Long> followerIds = followService.getFollowerIds(loginUser.getId());

            // 현재 시간을 타임스탬프로 사용
            Double currentTime = getCurrentTimeMillis();

            // 각 팔로워의 타임라인에 게시물 추가
            followerIds.forEach(followerId -> {
                String timelineKey = cacheKeyGenerator(TIMELINE, USER_ID, followerId.toString());
                redisService.addToSortedSet(timelineKey, currentTime, savedPost.getId());
            });
        }
    }
}
