package com.pigeon_stargram.sns_clone.domain.search;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.pigeon_stargram.sns_clone.util.LocalDateTimeUtil.getCurrentTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "search_history")
public class SearchHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String searchQuery;

    public void updateModifiedDate() {
        setModifiedDate(getCurrentTime());
    }

    protected void setModifiedDate(LocalDateTime modifiedDate) {
        super.setModifiedDate(modifiedDate);
    }

}
