package com.pigeon_stargram.sns_clone.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.UserBaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.notification.Notification;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString(exclude = {"followers", "followings", "receivedNotifications"})
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_entity")
@Entity
public class User extends UserBaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    @Column(unique = true)
    private String workEmail;
    private String personalEmail;
    private String workPhone;
    private String personalPhone;
    private String location;
    private String avatar;
    private String birthdayText;
    private String onlineStatus;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    private List<Follow> followers;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Follow> followings;

    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    private List<Notification> receivedNotifications;

    public User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public User update(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
        return this;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}
