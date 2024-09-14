package com.pigeon_stargram.sns_clone.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * User 엔티티는 애플리케이션 사용자의 로그인, 프로필 정보를 나타냅니다.
 * - RDB의 USER_ENTITY 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "user_entity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"followers", "followings"})
@EqualsAndHashCode
public class User extends UserBaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 로그인 정보
     */
    @Column(unique = true)          // email이 로그인 ID로 사용되므로 중복 불가
    private String workEmail;       // Google OAuth 로그인에서 사용하는 email
    @JsonIgnore                     // 비밀번호가 캐시되지 않도록 JSON 직렬화 대상에서 제외
    private String password;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;  // API 접근 권한을 결정하는 필드

    /**
     * 사용자 프로필 정보
     */
    private String name;
    private String company;
    private String avatar;
    private String personalPhone;
    private String workPhone;
    private String personalEmail;
    private String location;
    private String birthdayText;
    private String onlineStatus;    // 채팅에서 확인할 수 있는 현재 접속 정보

    /**
     * 연관관계 정보
     */
    @OneToMany(mappedBy = "recipient")
    @JsonIgnore
    private List<Follow> followers;     // 나를 팔로우중인 유저에 대한 Join Table의 엔티티
    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private List<Follow> followings;    // 내가 팔로우중인 유저에 대한 Join Table의 엔티티

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

}