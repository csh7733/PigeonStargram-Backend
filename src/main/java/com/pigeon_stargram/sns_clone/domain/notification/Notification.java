package com.pigeon_stargram.sns_clone.domain.notification;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = {"sender", "recipient"})
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
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
    private Boolean isRead;
    private Long sourceId;
    private Long sourceId2;

    public void setRead(Boolean read) {
        isRead = read;
    }
}
