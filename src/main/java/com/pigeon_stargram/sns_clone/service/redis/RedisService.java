package com.pigeon_stargram.sns_clone.service.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    /**
     * 메시지 큐에 태스크를 추가합니다.
     *
     * @param queueName 큐의 이름
     * @param task      추가할 태스크 (Object 타입)
     */
    public void pushTask(String queueName, Object task) {
        redisTemplate.opsForList().leftPush(queueName, task);
    }

    /**
     * 메시지 큐에서 태스크를 블로킹 팝 방식으로 가져옵니다.
     *
     * @param queueName 큐의 이름
     * @param timeout   초과 대기 시간
     * @return 가져온 태스크 (Object 타입)
     */
    public Object popTask(String queueName, Duration timeout) {
        return redisTemplate.opsForList().rightPop(queueName, timeout);
    }

    /**
     * 메시지 큐에서 블로킹 팝 방식으로 태스크를 가져오며 대기시간을 무한으로 설정합니다.
     *
     * @param queueName 큐의 이름
     * @return 가져온 태스크 (Object 타입)
     */
    public Object popTask(String queueName) {
        return popTask(queueName, Duration.ZERO);
    }

    /**
     * Set에 값을 추가합니다.
     *
     * @param setKey Set의 키
     * @param value  추가할 값
     */
    public void addToSet(String setKey, Object value) {
        redisTemplate.opsForSet().add(setKey, value);
    }

    /**
     * Set에 List전체 값을 추가합니다.
     *
     * @param setKey Set의 키
     * @param values 추가할 List
     */
    public <T> void addAllToSet(String setKey, List<T> values) {
        redisTemplate.opsForSet().add(setKey, values.toArray());
    }

    /**
     * Set에서 값을 제거합니다.
     *
     * @param setKey Set의 키
     * @param value  제거할 값
     */
    public void removeFromSet(String setKey, Object value) {
        redisTemplate.opsForSet().remove(setKey, value);
    }


    /**
     * Set의 모든 값을 제거합니다.
     *
     * @param setKey Set의 키
     */
    public void removeSet(String setKey) {
        redisTemplate.delete(setKey);
    }

    /**
     * Set에서 특정 값의 존재 여부를 확인합니다.
     *
     * @param setKey Set의 키
     * @param value  확인할 값
     * @return 값의 존재 여부
     */
    public Boolean isMemberOfSet(String setKey, Object value) {
        return redisTemplate.opsForSet().isMember(setKey, value);
    }

    /**
     * Set의 모든 값들을 Set으로 가져옵니다.
     *
     * @param setKey Set의 키
     * @return 키에 해당하는 Set
     */
    public Set<Object> getSet(String setKey) {
        return redisTemplate.opsForSet().members(setKey);
    }

    /**
     * Set의 모든 값들을 Long 타입 리스트로 변환하여 가져옵니다.
     *
     * @param setKey Set의 키
     * @return 키에 해당하는 Set을 Long 타입 리스트로 변환한 결과
     */
    public List<Long> getSetAsLongList(String setKey) {
        return getSet(setKey).stream()
                .map(object -> Long.valueOf((Integer) object))
                .collect(Collectors.toList());
    }

    /**
     * Set의 모든 값들을 Long 타입 리스트로 변환하고 더미 데이터를 제거하여 가져옵니다.
     *
     * @param setKey Set의 키
     * @return 키에 해당하는 Set을 Long 타입 리스트로 변환하고 더미데이터 0L을 제거한 결과
     */
    public List<Long> getSetAsLongListExcludeDummy(String setKey) {
        return getSetAsLongList(setKey).stream()
                .filter(value -> !value.equals(0L))
                .collect(Collectors.toList());
    }

    /**
     * Set의 원소 갯수를 가져옵니다.
     *
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
     * @param fieldKey     Redis Hash 내의 필드 키
     * @param value        저장할 값 (객체)
     */
    public void putValueInHash(String redisHashKey, String fieldKey, Object value) {
        redisTemplate.opsForHash().put(redisHashKey, fieldKey, value);
    }

    /**
     * Redis Hash에서 값을 가져와 지정된 타입으로 반환합니다.
     * 기본 직렬화기를 사용하여 객체를 역직렬화합니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @param fieldKey     Redis Hash 내의 필드 키
     * @param clazz        반환할 타입의 클래스
     * @param <T>          반환할 타입
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
     * Redis Hash에서 모든 필드와 값을 가져옵니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @return 모든 필드와 값이 포함된 맵을 반환합니다.
     */
    public Map<Object, Object> getAllFieldsFromHash(String redisHashKey) {
        return redisTemplate.opsForHash().entries(redisHashKey);
    }

    /**
     * Redis Hash에서 특정 필드를 제거합니다.
     *
     * @param redisHashKey Redis Hash의 키
     * @param fieldKey     제거할 필드의 키
     */
    public void removeFieldFromHash(String redisHashKey, String fieldKey) {
        redisTemplate.opsForHash().delete(redisHashKey, fieldKey);
    }

    /**
     * 레디스에 key에 해당하는 자료구조가 있는지 확인합니다.
     *
     * @param key 자료구조 키
     * @return 키에 해당하는 자료구조 존재여부
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 레디스의 key:value 에서 값을 가져옵니다.
     *
     * @param key - 키값
     * @return 키에 해당하는 값
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis Hash에서 특정 필드의 존재 여부를 확인합니다.
     *
     * @param hashKey  Hash의 키
     * @param fieldKey 확인할 필드의 키
     * @return 필드의 존재 여부
     */
    public Boolean hasFieldInHash(String hashKey, String fieldKey) {
        return redisTemplate.opsForHash().hasKey(hashKey, fieldKey);
    }

    /**
     * Redis Hash에서 특정 필드의 값을 증가시킵니다.
     *
     * @param hashKey  Hash의 키
     * @param fieldKey 증가시킬 필드의 키
     * @param delta    증가시킬 값
     * @return 증가된 값
     */
    public Long incrementHashValue(String hashKey, String fieldKey, long delta) {
        return redisTemplate.opsForHash().increment(hashKey, fieldKey, delta);
    }

    /**
     * Redis 채널에 메시지를 발행합니다.
     *
     * @param channelName 채널의 이름
     * @param message     발행할 메시지 (Object 타입)
     */
    public void publishMessage(String channelName, Object message) {
        redisTemplate.convertAndSend(channelName, message);
    }

    /**
     * Redis 패턴을 구독하여 메시지를 수신합니다.
     *
     * @param pattern  구독할 패턴 (예: "chat.*.*")
     * @param listener 메시지를 수신할 리스너
     */
    public void subscribeToPattern(String pattern, MessageListener listener) {
        redisMessageListenerContainer.addMessageListener(listener, new PatternTopic(pattern));
    }

    /**
     * Redis 패턴 구독을 해제합니다.
     *
     * @param pattern  구독을 해제할 패턴 (예: "chat.*.*")
     * @param listener 메시지를 수신할 리스너
     */
    public void unsubscribeFromPattern(String pattern, MessageListener listener) {
        redisMessageListenerContainer.removeMessageListener(listener, new PatternTopic(pattern));
    }

    /**
     * 주어진 바이트 배열을 지정된 클래스 타입의 객체로 역직렬화합니다.
     *
     * @param messageBody 역직렬화할 바이트 배열입니다.
     * @param clazz       바이트 배열을 변환할 클래스 타입입니다.
     * @param <T>         반환할 객체의 타입을 나타냅니다.
     * @return 지정된 타입으로 변환된 역직렬화된 객체를 반환합니다.
     * @throws IllegalArgumentException 역직렬화된 객체를 지정된 클래스 타입으로 변환할 수 없을 경우 발생합니다.
     */
    public <T> T deserializeMessage(byte[] messageBody, Class<T> clazz) {
        Object deserialized = redisTemplate.getValueSerializer().deserialize(messageBody);

        if (clazz.isInstance(deserialized)) {
            return clazz.cast(deserialized);
        } else {
            throw new IllegalArgumentException("변환할 수 없습니다. " + clazz.getName());
        }
    }

    /**
     * Sorted Set에 값을 추가합니다.
     *
     * @param setKey Sorted Set의 키
     * @param score  정렬 기준이 될 점수 (예: 타임스탬프)
     * @param value  추가할 값
     */
    public void addToSortedSet(String setKey, double score, Object value) {
        redisTemplate.opsForZSet().add(setKey, value, score);
    }

    /**
     * Sorted Set에서 특정 범위 내의 값을 가져옵니다.
     *
     * @param setKey     Sorted Set의 키
     * @param startScore 시작 점수
     * @param endScore   종료 점수
     * @return 점수 범위 내의 값들의 Set
     */
    public Set<Object> getRangeByScore(String setKey, double startScore, double endScore) {
        return redisTemplate.opsForZSet().rangeByScore(setKey, startScore, endScore);
    }

    /**
     * Redis의 Sorted Set에서 상위 N개의 값을 점수순으로 가져와 지정된 타입으로 반환합니다.
     *
     * @param setKey Sorted Set의 키
     * @param count  가져올 값의 개수
     * @param clazz  반환할 값의 타입
     * @param <T>    반환할 타입
     * @return 상위 N개의 값을 지정된 타입으로 변환하여 List로 반환
     */
    public <T> List<T> getTopNFromSortedSet(String setKey, int count, Class<T> clazz) {
        Set<Object> rawResults = redisTemplate.opsForZSet().reverseRange(setKey, 0, count - 1);

        if (rawResults == null) {
            return Collections.emptyList();
        }

        return rawResults.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    /**
     * Sorted Set에서 특정 값을 제거합니다.
     *
     * @param setKey Sorted Set의 키
     * @param value  제거할 값
     */
    public void removeFromSortedSet(String setKey, Object value) {
        redisTemplate.opsForZSet().remove(setKey, value);
    }

    /**
     * Sorted Set에서 값을 삭제합니다.
     *
     * @param setKey Sorted Set의 키
     */
    public void removeSortedSet(String setKey) {
        redisTemplate.delete(setKey);
    }

    /**
     * Sorted Set의 크기를 가져옵니다.
     *
     * @param setKey Sorted Set의 키
     * @return Sorted Set의 원소 개수
     */
    public Long getSortedSetSize(String setKey) {
        return redisTemplate.opsForZSet().size(setKey);
    }

    /**
     * Redis에서 주어진 키에 해당하는 Sorted Set이 존재하는지 확인합니다.
     *
     * @param setKey 확인할 Sorted Set의 키
     * @return 키에 해당하는 Sorted Set이 존재하면 true, 존재하지 않으면 false
     */
    public Boolean isSortedSetExists(String setKey) {
        // Redis에서 해당 키가 존재하는지 확인
        return redisTemplate.hasKey(setKey);
    }

    /**
     * Sorted Set의 값들을 순위를 확인해 Set으로 가져옵니다.
     *
     * @param key   Sorted Set의 키
     * @param start 시작 순위
     * @param end   끝나는 순위
     * @return 키에 해당하는 Sorted Set
     */
    public Set<Object> getSortedSetRangeByRank(String key, Integer start, Integer end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * Sorted Set의 값들을 순위를 확인해 List로 가져옵니다.
     *
     * @param key   Sorted Set의 키
     * @param start 시작 순위
     * @param end   끝나는 순위
     * @return 키에 해당하는 Sorted Set을 List로 변환한 결과
     */
    public List<Long> getSortedSetRangeByRankAsList(String key, Integer start, Integer end) {
        return getSortedSetRangeByRank(key, start, end).stream()
                .map(object -> Long.valueOf((Integer) object))
                .collect(Collectors.toList());
    }

    /**
     * Sorted Set의 값들을 순위를 확인해 더미데이터를 제거하여 List로 가져옵니다.
     *
     * @param key   Sorted Set의 키
     * @param start 시작 순위
     * @param end   끝나는 순위
     * @return 키에 해당하는 Sorted Set을 List로 변환하고 더미데이터를 제거한 결과
     */
    public List<Long> getSortedSetRangeByRankAsListExcludeDummy(String key, Integer start, Integer end) {
        return getSortedSetRangeByRankAsList(key, start, end).stream()
                .filter(value -> !value.equals(0L))
                .collect(Collectors.toList());
    }

    /**
     * Redis의 Sorted Set에서 특정 값에 해당하는 점수를 가져와 지정된 타입으로 반환합니다.
     *
     * @param setKey Sorted Set의 키
     * @param value  점수를 가져올 값
     * @param clazz  반환할 타입의 클래스
     * @param <T>    반환할 타입
     * @return 해당 값에 대한 점수(타임스탬프)를 지정된 타입으로 변환하여 반환, 존재하지 않으면 null
     */
    public <T> T getScoreFromSortedSet(String setKey, Object value, Class<T> clazz) {
        Double score = redisTemplate.opsForZSet().score(setKey, value);

        if (score == null) {
            return null;
        }

        if (clazz.isInstance(score)) {
            return clazz.cast(score);
        }

        throw new IllegalArgumentException("변환할 수 없습니다. " + clazz.getName());
    }

    /**
     * Redis의 Sorted Set에서 특정 값의 점수를 증가시킵니다.
     *
     * @param setKey Sorted Set의 키
     * @param value  점수를 증가시킬 값
     * @param delta  증가시킬 점수
     * @return 증가된 후의 점수
     */
    public Double incrementScoreInSortedSet(String setKey, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(setKey, value, delta);
    }

    /**
     * List<Long>에 더미데이터를 추가하여 Redis의 Set에 캐시하고 원본 List를 반환합니다.
     *
     * @param list     캐시할 데이터의 List
     * @param cacheKey 캐시 Set의 Key
     * @return 원본 List
     */
    public List<Long> cacheListToSetWithDummy(List<Long> list,
                                              String cacheKey) {
        list.add(0L);
        addAllToSet(cacheKey, list);

        list.remove(0L);
        return list;
    }

    /**
     * Sorted Set에 List전체 값을 추가합니다.
     *
     * @param key Sorted Set의 키
     * @param list 추가할 List
     */
    public void addAllToSortedSet(String key,
                                  List<ZSetOperations.TypedTuple<Object>> list) {
        redisTemplate.opsForZSet().add(key, new HashSet<>(list));
    }

    /**
     * List<DefaultTypedTuple<Long>>에 더미데이터를 추가하여 Redis의 Sorted Set에 캐시하고
     * 원본 value List를 반환합니다.
     *
     * @param list     캐시할 데이터의 List
     * @param cacheKey 캐시 Sorted Set의 Key
     * @return 원본 List
     */
    public List<Object> cacheListToSortedSetWithDummy(List<ZSetOperations.TypedTuple<Object>> list,
                                                                                 String cacheKey) {
        ZSetOperations.TypedTuple<Object> dummy = new DefaultTypedTuple<>(0L, 0D);

        list.add(dummy);
        addAllToSortedSet(cacheKey, list);

        list.remove(dummy);
        return list.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());
    }

}
