package com.pigeon_stargram.sns_clone.domain.comment;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;

public class CommentFactory {

    public static Comment createComment(CreateCommentDto dto,
                                        User loginUser,
                                        Post post) {
        return Comment.builder()
                .user(loginUser)
                .post(post)
                .content(dto.getContent())
                .build();
    }

    public static CommentLike createCommentLike(User loginUser,
                                                Comment comment) {
        return CommentLike.builder()
                .user(loginUser)
                .comment(comment)
                .build();
    }
}
