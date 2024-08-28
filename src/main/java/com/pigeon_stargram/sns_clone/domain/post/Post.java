package com.pigeon_stargram.sns_clone.domain.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "POSTS")
@Entity
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

    @Transient
    private List<Image> imagesForSerialization = new ArrayList<>();

    //CascadeType.Remove를 위해 필요한 필드(실제로는 사용X)
    //fetch = FetchType.LAZY(기본값)로 두어 프록시를 fetch 하지않음
    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder
    public Post(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public void modify(String content) {
        this.content = content;
    }

    public void modify(String content,List<Image> images) {
        this.content = content;
        this.images = images;
    }

    @PostLoad
    private void copyImages() {
        this.imagesForSerialization = new ArrayList<>(this.images);
    }

    @PrePersist
    @PreUpdate
    private void synchronizeImages() {
        this.images = new ArrayList<>(this.imagesForSerialization);
    }
}