package com.pigeon_stargram.sns_clone.domain.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    public SearchTerm(String term,String prefix){
        this.term = term;
        this.prefix = prefix;
        this.score = 0L;
    }

    public void updateScore(){
        this.score += 1;
    }

}
