package com.pigeon_stargram.sns_clone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 감사(Auditing) 기능을 활성화하기 위한 설정 클래스입니다.
 *
 * 이 클래스는 JPA 엔티티에서 생성일, 수정일 등의 필드를 자동으로 관리하기 위해
 * JPA Auditing 기능을 활성화합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

}
