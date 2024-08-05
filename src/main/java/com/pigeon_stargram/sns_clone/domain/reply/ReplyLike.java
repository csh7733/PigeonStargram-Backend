package com.pigeon_stargram.sns_clone.domain.reply;

import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reply_like")
public class ReplyLike {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @Builder
    public ReplyLike(User user, Reply reply) {
        this.user = user;
        this.reply = reply;
    }
}
