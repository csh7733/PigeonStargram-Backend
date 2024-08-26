package com.pigeon_stargram.sns_clone.service.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Transactional
@Service
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

}
