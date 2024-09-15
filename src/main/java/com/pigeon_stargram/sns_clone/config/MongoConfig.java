package com.pigeon_stargram.sns_clone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB 감사(Auditing) 기능을 활성화하기 위한 설정 클래스입니다.
 *
 * 이 클래스는 MongoDB 컬렉션에서 생성일, 수정일 등의 필드를 자동으로 관리하기 위해
 * MongoDB Auditing 기능을 활성화합니다.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

}
