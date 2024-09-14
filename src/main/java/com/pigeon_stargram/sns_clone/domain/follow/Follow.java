package com.pigeon_stargram.sns_clone.domain.follow;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Follow extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isNotificationEnabled;  // Sender가 Recipient의 게시글 알림신청을 했는지 여부

    /**
     * 연관관계 정보
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Builder
    public Follow(User sender,
                  User recipient,
                  Boolean isNotificationEnabled) {
        this.sender = sender;
        this.recipient = recipient;
        this.isNotificationEnabled = isNotificationEnabled;
    }

    public void toggleNotificationEnabled() {
        isNotificationEnabled = !isNotificationEnabled;
    }
}
