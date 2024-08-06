package com.pigeon_stargram.sns_clone.domain.post;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
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
public class Posts extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

    private Integer likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostsLike> postsLikes = new ArrayList<>();

    @Builder
    public Posts(User user, String content) {
        this.user = user;
        this.content = content;
        this.likes = 0;
    }

    @Builder
    public Posts(User user, String content, List<Image> images) {
        this.user = user;
        this.content = content;
        this.images = images;
        this.likes = 0;
    }

    public void modify(String content) {
        this.content = content;
    }

    public void modify(String content,List<Image> images) {
        this.content = content;
        this.images = images;
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