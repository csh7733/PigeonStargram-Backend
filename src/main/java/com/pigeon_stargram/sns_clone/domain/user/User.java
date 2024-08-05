package com.pigeon_stargram.sns_clone.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
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

    public User(String id, String name, String company, String role, String workEmail, String personalEmail, String workPhone, String personalPhone, String location, String avatar, String status, String birthdayText, String onlineStatus) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.company = company;
        this.role = role;
        this.workEmail = workEmail;
        this.personalEmail = personalEmail;
        this.workPhone = workPhone;
        this.personalPhone = personalPhone;
        this.location = location;
        this.avatar = avatar;
        this.status = status;
        this.birthdayText = birthdayText;
        this.onlineStatus = onlineStatus;
    }
}
