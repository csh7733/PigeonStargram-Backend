package com.pigeon_stargram.sns_clone.domain.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
/**
 * Image 엔티티는 게시물(Post)에 포함된 개별 이미지를 나타냅니다.
 * 이 엔티티는 데이터베이스에 저장되어 게시물과 이미지 간의 연관 관계를 관리합니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String img;         // 이미지의 URL
    private Boolean featured;   // 해당 이미지가 게시물의 대표 이미지인지 여부

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;          // 해당 이미지가 속한 게시물(Post)과의 연관 관계

    public Image(String img, Boolean featured) {
        this.img = img;
        this.featured = featured;
    }
}
