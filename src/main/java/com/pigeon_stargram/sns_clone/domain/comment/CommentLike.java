package com.pigeon_stargram.sns_clone.domain.comment;

import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글에 대한 좋아요를 나타내는 엔티티 클래스입니다.
 * 사용자와 댓글 간의 좋아요 관계를 관리합니다.
 */
@Entity
@Table(name = "comment_like")
@Getter
@NoArgsConstructor
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요를 누른 사용자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 좋아요가 달린 댓글
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public CommentLike(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
