package com.pigeon_stargram.sns_clone.domain.search;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.convertDoubleToLocalDateTime;
import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTime;

/**
 * SearchHistory 엔티티는 사용자가 검색한 기록을 저장합니다.
 * 사용자, 검색어, 그리고 검색 기록의 점수(우선순위)를 저장합니다.
 * RDB의 `search_history` 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "search_history")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SearchHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String searchQuery; // 사용자가 검색한 쿼리

    private Double score; // 검색 기록의 날짜를 score로 수치화(즉 최신 날짜가 더 높은 우선순위)

    public void updateScore(Double score) {
        this.score = score;
    }

    protected void setModifiedDate(LocalDateTime modifiedDate) {
        super.setModifiedDate(modifiedDate);
    }

}
