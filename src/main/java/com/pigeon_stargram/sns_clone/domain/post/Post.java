package com.pigeon_stargram.sns_clone.domain.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Post 클래스는 게시물과 관련된 데이터를 관리하는 엔티티로,
 * 사용자가 작성한 게시물의 내용, 작성자, 관련된 이미지, 좋아요 정보 등을 포함합니다.
 */
@Entity
@Table(name = "POSTS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;     // 게시물의 내용
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;          // 게시물 작성자


    /**
     * 연관관계 정보
     */
    // 게시물에 좋아요를 누른 사용자에 대한 Join Table 엔티티
    // CascadeType.Remove를 위해 필요한 필드, 실제로는 사용하지 않는다.
    // fetch = FetchType.LAZY(기본값)로 두어 프록시를 fetch 하지않는다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<PostLike> postLikes = new ArrayList<>();   
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Image> images = new ArrayList<>(); // 게시물에 포함된 이미지

    // images를 캐시할 때 PersistenceBag(Hibernate Collection) 타입을 List로 변경하여 직렬화하기위한 필드
    // DB에는 저장하지 않는다.
    @Transient
    private List<Image> imagesForSerialization = new ArrayList<>();

    @Builder
    public Post(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public void editContent(String content) {
        this.content = content;
    }

    // DB에서 게시물을 로드한 후 이미지를 복사하는 메서드 (직렬화용)
    @PostLoad
    private void copyImages() {
        this.imagesForSerialization = new ArrayList<>(this.images);
    }

    // 게시물을 저장하기 전에 이미지를 동기화하는 메서드
    @PrePersist
    private void synchronizeImages() {
        this.images = new ArrayList<>(this.imagesForSerialization);
    }
}