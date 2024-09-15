package com.pigeon_stargram.sns_clone.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.*;

/**
 * Redis 캐시 구성을 위한 설정 클래스입니다.
 *
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // LocalDateTime을 JSON으로 직렬화하는 옵션 설정
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 API 모듈 등록
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 날짜를 타임스탬프로 쓰지 않도록 설정

        // Hibernate 클래스 직렬화 옵션 설정
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        hibernate6Module.configure(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION, false);  // Transient 필드 직렬화 비활성화
        mapper.registerModule(hibernate6Module);  // Hibernate 모듈 등록

        // 객체 타입 직렬화를 위한 기본 타입 설정
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);  // 모든 비최종 클래스에 대해 직렬화 활성화

        // RedisCacheConfiguration 설정
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // 캐시 데이터 TTL(만료 시간)을 10분으로 설정
                .disableCachingNullValues()  // null 값 캐시 비활성화
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer(mapper)))  // 값 직렬화를 Jackson을 사용해 JSON으로 처리
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()));  // 키 직렬화를 String으로 처리

        // RedisCacheManager를 반환하여 Redis 캐시 관리를 처리
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)  // 기본 캐시 구성 적용
                .build();
    }
}
