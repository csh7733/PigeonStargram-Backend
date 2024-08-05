package com.pigeon_stargram.sns_clone.domain.user;

import com.pigeon_stargram.sns_clone.dto.chat.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString(exclude = {"postsList", "followList"})
@EqualsAndHashCode
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String role;
    private String workEmail;
    private String personalEmail;
    private String workPhone;
    private String personalPhone;
    private String location;
    private String avatar;
    private String status;
    private String birthdayText;
    private String onlineStatus;

    @OneToMany(mappedBy = "user")
    private List<Posts> postsList;

    @OneToMany(mappedBy = "user")
    private List<Follow> followList;


    @Builder
    public User(String name) {
        this.name = name;
    }
}
