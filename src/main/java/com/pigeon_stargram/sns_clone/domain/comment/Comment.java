package com.pigeon_stargram.sns_clone.domain.comment;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
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

    //CascadeType.Remove를 위해 필요한 필드(실제로는 사용X)
    //fetch = FetchType.LAZY(기본값)로 두어 프록시를 fetch 하지않음
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

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
