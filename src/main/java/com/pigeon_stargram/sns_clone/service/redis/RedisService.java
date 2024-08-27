package com.pigeon_stargram.sns_clone.service.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 메시지 큐에 태스크를 추가합니다.
     * @param queueName 큐의 이름
     * @param task 추가할 태스크 (Object 타입)
     */
    public void pushTask(String queueName, Object task) {
        redisTemplate.opsForList().leftPush(queueName, task);
    }

    /**
     * 메시지 큐에서 태스크를 블로킹 팝 방식으로 가져옵니다.
     * @param queueName 큐의 이름
     * @param timeout 초과 대기 시간
     * @return 가져온 태스크 (Object 타입)
     */
    public Object popTask(String queueName, Duration timeout) {
        return redisTemplate.opsForList().rightPop(queueName, timeout);
    }

    /**
     * 메시지 큐에서 블로킹 팝 방식으로 태스크를 가져오며 대기 시간이 없도록 합니다.
     * @param queueName 큐의 이름
     * @return 가져온 태스크 (Object 타입)
     */
    public Object popTask(String queueName) {
        return popTask(queueName, Duration.ZERO);
    }

    /**
     * Set에 값을 추가합니다.
     * @param setKey Set의 키
     * @param value 추가할 값
     */
    public void addToSet(String setKey, Object value) {
        redisTemplate.opsForSet().add(setKey, value);
    }

    /**
     * Set에서 값을 제거합니다.
     * @param setKey Set의 키
     * @param value 제거할 값
     */
    public void removeFromSet(String setKey, Object value) {
        redisTemplate.opsForSet().remove(setKey, value);
    }

    /**
     * Set에서 특정 값의 존재 여부를 확인합니다.
     * @param setKey Set의 키
     * @param value 확인할 값
     * @return 값의 존재 여부
     */
    public Boolean isMemberOfSet(String setKey, Object value) {
        return redisTemplate.opsForSet().isMember(setKey, value);
    }

    /**
     * Set의 원소 갯수를 가져옵니다.
     * @param setKey Set의 키
     * @return Set에 포함된 원소의 갯수
     */
    public Long getSetSize(String setKey) {
        return redisTemplate.opsForSet().size(setKey);
    }

    /**
     * Redis Hash에 값을 저장합니다.
     * 기본 직렬화기를 사용하여 객체를 직렬화한 후 Redis에 저장합니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @param fieldKey Redis Hash 내의 필드 키
     * @param value 저장할 값 (객체)
     */
    public void putValueInHash(String redisHashKey, String fieldKey, Object value) {
        redisTemplate.opsForHash().put(redisHashKey, fieldKey, value);
    }

    /**
     * Redis Hash에서 값을 가져와 지정된 타입으로 반환합니다.
     * 기본 직렬화기를 사용하여 객체를 역직렬화합니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @param fieldKey Redis Hash 내의 필드 키
     * @param clazz 반환할 타입의 클래스
     * @param <T> 반환할 타입
     * @return 지정된 타입으로 변환된 값, 또는 null (값이 없을 경우)
     */
    public <T> T getValueFromHash(String redisHashKey, String fieldKey, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(redisHashKey, fieldKey);

        if (value == null) {
            return null;
        }

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }

        // Long타입을 원할때, Redis에서 Integer로 반환할 수 있기때문에 변환 과정이 필요
        if (clazz == Long.class && value instanceof Integer) {
            return clazz.cast(((Integer) value).longValue());
        }

        throw new IllegalArgumentException("변환할 수 없습니다. " + clazz.getName());
    }

    /**
     * Redis Hash에서 특정 필드를 제거합니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @param fieldKey 제거할 필드의 키
     */
    public void removeFieldFromHash(String redisHashKey, String fieldKey) {
        redisTemplate.opsForHash().delete(redisHashKey, fieldKey);
    }


}
