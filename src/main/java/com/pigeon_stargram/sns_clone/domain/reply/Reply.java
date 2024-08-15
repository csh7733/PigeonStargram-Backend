package com.pigeon_stargram.sns_clone.domain.reply;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
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
@Table(name = "reply")
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String content;

    //CascadeType.Remove를 위해 필요한 필드(실제로는 사용X)
    //fetch = FetchType.LAZY(기본값)로 두어 프록시를 fetch 하지않음
    @OneToMany(mappedBy = "reply", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReplyLike> replyLikes = new ArrayList<>();

    @Builder
    public Reply(User user, Comment comment, String content) {
        this.user = user;
        this.comment = comment;
        this.content = content;
    }

    public void modify(String content) {
        this.content = content;
    }
}
