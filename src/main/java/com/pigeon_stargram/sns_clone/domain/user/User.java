package com.pigeon_stargram.sns_clone.domain.user;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString(exclude = {"followers", "followings"})
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_entity")
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String workEmail;
    private String personalEmail;
    private String workPhone;
    private String personalPhone;
    private String location;
    private String avatar;
    private String status;
    private String birthdayText;
    private String onlineStatus;

    @Enumerated(EnumType.STRING)
    private Role role;

//    @OneToMany(mappedBy = "user")
//    private List<Posts> posts;

    @OneToMany(mappedBy = "fromUser")
    private List<Follow> followers;

    @OneToMany(mappedBy = "toUser")
    private List<Follow> followings;

    public User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }
}
