package com.pigeon_stargram.sns_clone.domain.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class NotificationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private NotificationType type;
    private String message;
    private Long sourceId;
    private Long sourceId2;
}
