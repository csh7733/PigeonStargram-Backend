package com.pigeon_stargram.sns_clone.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정을 위한 구성 클래스입니다.
 *
 * 이 클래스는 RedisTemplate과 Redis 메시지 리스너 컨테이너를 설정하여
 * Redis와의 통신을 관리하고, Redis에서 사용하는 데이터의 직렬화 방식을 정의합니다.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // LocalDateTime을 JSON으로 직렬화하는 옵션 설정
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 API 모듈 등록
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 날짜를 타임스탬프로 쓰지 않도록 설정

        // 객체 타입 직렬화를 위한 기본 타입 설정
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);  // 모든 비최종 클래스에 대해 직렬화 활성화

        // RedisTemplate에 연결 팩토리 설정
        template.setConnectionFactory(redisConnectionFactory);
        // 키와 해시 키를 String으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // 값과 해시 값을 Jackson을 사용해 JSON으로 직렬화
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}