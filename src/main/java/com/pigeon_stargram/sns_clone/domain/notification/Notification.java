package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Entity
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    private NotificationType type;
    private String message;
    private String redirectUrl;
    private Boolean isRead;

    @Builder
    public Notification(User sender, User recipient,
                        NotificationType type, String message,
                        String redirectUrl, Boolean isRead) {
        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.redirectUrl = redirectUrl;
        this.isRead = isRead;
    }
}
