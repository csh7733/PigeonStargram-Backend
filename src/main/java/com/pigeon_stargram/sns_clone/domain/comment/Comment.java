package com.pigeon_stargram.sns_clone.domain.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 엔티티 클래스입니다.
 * 댓글의 내용, 작성자, 게시물, 좋아요 목록을 관리합니다.
 */
@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 댓글 내용

    /**
     * 연관관계 정보
     */
    // 댓글에 달린 좋아요 목록
    // CascadeType.Remove를 위해 필요한 필드(실제로는 사용X)
    // fetch = FetchType.LAZY(기본값)로 두어 프록시를 fetch 하지않음
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<CommentLike> commentLikes = new ArrayList<>();

    // 댓글 작성자
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // 댓글이 달린 게시물
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @Builder
    public Comment(User user, Post post, String content) {
        this.user = user;
        this.post = post;
        this.content = content;
    }

    public void editContent(String content) {
        this.content = content;
    }
}
