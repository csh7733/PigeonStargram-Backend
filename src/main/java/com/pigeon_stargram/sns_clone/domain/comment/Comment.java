package com.pigeon_stargram.sns_clone.domain.comment;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts post;

    private String content;

    private Integer likes;

    @Builder
    public Comment(User user, Posts post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.likes = 0;
    }

    public void modify(String content) {
        this.content = content;
    }

    public void incrementLikes() {
        if (this.likes == null) {
            this.likes = 0;
        }
        this.likes += 1;
    }

    public void decrementLikes() {
        if (this.likes != null && this.likes > 0) {
            this.likes -= 1;
        }
    }
}
