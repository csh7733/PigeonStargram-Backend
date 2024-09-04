package com.pigeon_stargram.sns_clone.domain.notification;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class NotificationV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private NotificationContent content;

    private Boolean isRead;

    public void setRead(Boolean read) {
        isRead = read;
    }
}
