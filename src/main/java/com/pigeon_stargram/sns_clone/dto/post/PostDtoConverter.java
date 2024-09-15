package com.pigeon_stargram.sns_clone.dto.post;

import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
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

public class PostDtoConverter {

    public static CreatePostDto toCreatePostDto(RequestCreatePostDto dto,
                                                SessionUser loginUser,
                                                List<String> imageUrls,
                                                String fieldKey) {
        return CreatePostDto.builder()
                .loginUserId(loginUser.getId())
                .content(dto.getContent())
                .imageUrls(imageUrls)
                .fieldKey(fieldKey)
                .hasImage(dto.getHasImage())
                .taggedUserIds(dto.getTaggedUserIds())
                .build();
    }

    public static EditPostDto toEditPostDto(RequestEditPostDto dto,
                                            Long postId) {
        return EditPostDto.builder()
                .postId(postId)
                .content(dto.getContent())
                .build();
    }

    public static LikePostDto toLikePostDto(RequestLikePostDto dto,
                                            SessionUser loginUser) {
        return LikePostDto.builder()
                .loginUserId(loginUser.getId())
                .postId(dto.getPostId())
                .writerId(dto.getPostUserId())
                .build();
    }

    public static ResponsePostDto toResponsePostDto(PostContentDto contentDto,
                                                    PostLikeDto likeDto,
                                                    List<ResponseCommentDto> commentDtos,
                                                    Boolean isMoreComments) {
        return ResponsePostDto.builder()
                .id(contentDto.getId())
                .profile(contentDto.getProfile())
                .data(toPostDataDto(contentDto, likeDto, commentDtos, isMoreComments))
                .build();
    }

    public static PostDataDto toPostDataDto(PostContentDto contentDto,
                                            PostLikeDto likeDto,
                                            List<ResponseCommentDto> commentDtos,
                                            Boolean isMoreComments) {
        return PostDataDto.builder()
                .content(contentDto.getContent())
                .images(contentDto.getImages())
                .likes(likeDto)
                .comments(commentDtos)
                .isMoreComments(isMoreComments)
                .build();
    }

    public static PostContentDto toPostContentDto(Post post) {
        List<ImageDto> imageDtos = post.getImages().stream()
                .map(PostDtoConverter::toImageDto)
                .collect(Collectors.toList());
        PostProfileDto profileDto = toPostProfileDto(post.getUser(), post.getModifiedDate());
        return PostContentDto.builder()
                .id(post.getId())
                .profile(profileDto)
                .content(post.getContent())
                .images(imageDtos)
                .build();
    }

    public static PostProfileDto toPostProfileDto(User user,
                                                  LocalDateTime modifiedDate) {
        return PostProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatar(user.getAvatar())
                .time(LocalDateTimeUtil.formatTime(modifiedDate))
                .build();

    }

    public static ImageDto toImageDto(Image image) {
        return ImageDto.builder()
                .img(image.getImg())
                .featured(image.getFeatured())
                .build();
    }

    public static PostLikeDto toPostLikeDto(Boolean like,
                                            Integer value) {
        return PostLikeDto.builder()
                .like(like)
                .value(value)
                .build();
    }

    public static NotifyPostTaggedDto toNotifyPostTaggedDto(CreatePostDto dto,
                                                            User loginUser) {
        return NotifyPostTaggedDto.builder()
                .userId(loginUser.getId())
                .userName(loginUser.getName())
                .content(dto.getContent())
                .notificationRecipientIds(dto.getTaggedUserIds())
                .build();
    }
}
