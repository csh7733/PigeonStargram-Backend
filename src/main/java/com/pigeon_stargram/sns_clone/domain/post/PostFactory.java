package com.pigeon_stargram.sns_clone.domain.post;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;

public class PostFactory {

    public static Post createPost(CreatePostDto dto,
                                  User loginUser) {
        return Post.builder()
                .user(loginUser)
                .content(dto.getContent())
                .build();
    }

    public static Image createImage(String imageUrl,
                                    Post savedPost) {
        return Image.builder()
                .img(imageUrl)
                .featured(true)
                .post(savedPost)
                .build();
    }

    public static PostLike createPostLike(User user,
                                          Post post) {
        return PostLike.builder()
                .user(user)
                .post(post)
                .build();
    }
}
