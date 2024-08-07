package com.pigeon_stargram.sns_clone.domain.chat;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.comment.CommentLike;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    private String text;

}
