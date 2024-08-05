package com.pigeon_stargram.sns_clone.domain.comments;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.posts.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Comments extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts post;

    private String comment;

    private Integer likes;

    @Builder
    public Comments(User user, Posts post, String comment) {
        this.user = user;
        this.post = post;
        this.comment = comment;
        this.likes = 0;
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
