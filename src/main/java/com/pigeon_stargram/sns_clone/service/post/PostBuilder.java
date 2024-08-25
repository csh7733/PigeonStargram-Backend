package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.EditCommentDto;
import com.pigeon_stargram.sns_clone.dto.comment.response.ResponseCommentDto;
import com.pigeon_stargram.sns_clone.dto.notification.internal.NotifyPostTaggedDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.EditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.LikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.PostContentDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestCreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestEditPostDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.*;
import com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

public class PostBuilder {

    private PostBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static CreatePostDto buildCreatePostDto(RequestCreatePostDto dto,
                                                   SessionUser loginUser,
                                                   List<String> imageUrls) {
        return CreatePostDto.builder()
                .loginUserId(loginUser.getId())
                .content(dto.getContent())
                .imageUrls(imageUrls)
                .hasImage(dto.getHasImage())
                .taggedUserIds(dto.getTaggedUserIds())
                .build();
    }

    public static Post buildPost(CreatePostDto dto,
                                 User loginUser) {
        return Post.builder()
                .user(loginUser)
                .content(dto.getContent())
                .build();
    }

    public static NotifyPostTaggedDto buildNotifyPostTaggedDto(CreatePostDto dto,
                                                               User loginUser) {
        return NotifyPostTaggedDto.builder()
                .userId(loginUser.getId())
                .userName(loginUser.getName())
                .content(dto.getContent())
                .notificationRecipientIds(dto.getTaggedUserIds())
                .build();
    }

    public static EditPostDto buildEditPostDto(RequestEditPostDto dto,
                                               Long postId) {
        return EditPostDto.builder()
                .postId(postId)
                .content(dto.getContent())
                .build();
    }

    public static PostLike buildPostLike(User user,
                                         Post post) {
        return PostLike.builder()
                .user(user)
                .post(post)
                .build();
    }

    public static LikePostDto buildLikePostDto(RequestLikePostDto dto,
                                               SessionUser loginUser) {
        return LikePostDto.builder()
                .loginUserId(loginUser.getId())
                .postId(dto.getPostId())
                .writerId(dto.getPostUserId())
                .build();
    }

    public static ResponsePostDto buildResponsePostDto(PostContentDto contentDto,
                                                       PostLikeDto likeDto,
                                                       List<ResponseCommentDto> commentDtos) {
        return ResponsePostDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(buildPostDataDto(contentDto, likeDto, commentDtos))
                .build();
    }

    public static PostDataDto buildPostDataDto(PostContentDto contentDto,
                                               PostLikeDto likeDto,
                                               List<ResponseCommentDto> commentDtos) {
        return PostDataDto.builder()
                .content(contentDto.getContent())
                .images(contentDto.getImages())
                .likes(likeDto)
                .comments(commentDtos)
                .build();
    }

    public static PostContentDto buildPostContentDto(Post post) {
        List<ImageDto> imageDtos = post.getImages().stream()
                .map(PostBuilder::buildImageDto)
                .collect(Collectors.toList());
        PostProfileDto profileDto = buildPostProfileDto(post.getUser(), post.getModifiedDate());
        return PostContentDto.builder()
                .id(post.getId())
                .profile(profileDto)
                .content(post.getContent())
                .images(imageDtos)
                .build();
    }

    public static PostProfileDto buildPostProfileDto(User user,
                                                     LocalDateTime modifiedDate) {
        return PostProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .time(LocalDateTimeUtil.formatTime(modifiedDate))
                .build();

    }

    public static ImageDto buildImageDto(Image image) {
        return ImageDto.builder()
                .img(image.getImg())
                .featured(image.getFeatured())
                .build();
    }

    public static PostLikeDto buildPostLikeDto(Boolean like,
                                               Integer value) {
        return PostLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }
}
