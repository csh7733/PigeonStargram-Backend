package com.pigeon_stargram.sns_clone.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String avatar;

    public User(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }
}
