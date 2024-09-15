package com.pigeon_stargram.sns_clone.domain.reply;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * 답글(Reply) 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 댓글에 대한 답글을 나타내며, 답글의 내용, 답글을 작성한 사용자,
 * 상위 댓글(Comment)과의 연관관계, 그리고 답글에 달린 좋아요(ReplyLike) 정보 등을 포함하고 있습니다.
 * </p>
 */
@Entity
@Table(name = "reply")
@Getter
@NoArgsConstructor
@ToString
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content; // 답글 내용

    /**
     * 연관관계 정보
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;      // 답글을 작성한 사용자

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment; // 이 댓글이 속한 상위 댓글(Comment)
    
    // 댓글에 달린 좋아요를 관리하기 위한 리스트
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

    public void editContent(String content) {
        this.content = content;
    }
}
