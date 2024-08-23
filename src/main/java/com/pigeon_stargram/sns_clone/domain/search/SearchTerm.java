package com.pigeon_stargram.sns_clone.domain.search;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "search")
public class SearchTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String prefix; // key

    private String term; // value
    
    private Long score; // score

    public void updateScore(){
        this.score += 1;
    }

}
