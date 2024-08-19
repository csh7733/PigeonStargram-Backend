package com.pigeon_stargram.sns_clone.domain.post;

import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String img;
    private Boolean featured;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts posts;

    public Image(String img, Boolean featured) {
        this.img = img;
        this.featured = featured;
    }
}
