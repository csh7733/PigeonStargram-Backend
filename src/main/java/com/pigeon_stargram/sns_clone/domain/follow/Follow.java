package com.pigeon_stargram.sns_clone.domain.follow;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Entity
public class Follow extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private Boolean isNotificationEnabled;

    @Builder
    public Follow(User sender, User recipient, Boolean isNotificationEnabled) {
        this.sender = sender;
        this.recipient = recipient;
        this.isNotificationEnabled = isNotificationEnabled;
    }

    public void toggleNotificationEnabled() {
        isNotificationEnabled = !isNotificationEnabled;
    }
}
