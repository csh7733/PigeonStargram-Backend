package com.pigeon_stargram.sns_clone.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // User 가 캐시됐을 때, sender 에 대한 캐시 데이터 중복을 피하기 위한 필드
    @Transient
    private Long senderId;

    private NotificationType type;
    private String message;
    private Long sourceId;
    private Long sourceId2;

}
