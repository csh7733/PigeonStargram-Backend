package com.pigeon_stargram.sns_clone.domain.story;

import com.pigeon_stargram.sns_clone.domain.BaseTimeEntity;
import com.pigeon_stargram.sns_clone.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Story 엔티티는 사용자가 업로드하는 스토리 정보를 나타냅니다.
 * - 사용자, 스토리 이미지, 스토리 내용을 저장합니다.
 * - RDB의 STORY 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "story")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Story extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 스토리를 작성한 사용자

    @Column(nullable = true)
    private String content; // 스토리의 텍스트 내용 (Optional)

    @Column(nullable = false)
    private String img; // 스토리의 이미지 이름 (필수)

}
