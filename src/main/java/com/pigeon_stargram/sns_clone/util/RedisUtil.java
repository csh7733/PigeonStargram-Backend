package com.pigeon_stargram.sns_clone.util;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;

/**
 * Redis 키 생성 및 조작을 위한 유틸리티 클래스입니다.
 *
 * 이 클래스는 Redis에서 사용되는 다양한 키 생성 및 분리 작업을 처리하는 메서드를 제공합니다.
 */
public class RedisUtil {

    /**
     * 캐시 키를 생성하는 메서드입니다.
     *
     * @param value 키 값
     * @param prefix 키의 접두사
     * @param suffix 키의 접미사
     * @return 생성된 캐시 키 문자열
     */
    public static String cacheKeyGenerator(String value, String prefix, String suffix) {
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + suffix;
    }

    /**
     * 접미사 없이 캐시 키를 생성하는 메서드입니다.
     *
     * @param value 키 값
     * @param prefix 키의 접두사
     * @return 생성된 캐시 키 문자열
     */
    public static String cacheKeyGenerator(String value, String prefix) {
        return value + SEPARATOR_1 + prefix;
    }

    /**
     * 캐시 키 패턴을 생성하는 메서드입니다. 접두사와 함께 숫자가 포함된 패턴을 생성합니다.
     *
     * @param value 키 값
     * @param prefix 키의 접두사
     * @return 생성된 캐시 키 패턴
     */
    public static String cacheKeyPatternGenerator(String value, String prefix) {
        // 숫자가 하나 이상 포함된 패턴
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + ".+";
    }

    /**
     * HashKey와 FieldKey를 결합한 문자열을 반환하는 메서드입니다.
     *
     * @param hashKey HashKey
     * @param fieldKey FieldKey
     * @return 결합된 HashKey와 FieldKey 문자열
     */
    public static String combineHashKeyAndFieldKey(String hashKey, String fieldKey) {
        return hashKey + SEPARATOR_3 + fieldKey;
    }

    /**
     * 키에서 접미사를 추출하여 Long 타입으로 변환하는 메서드입니다.
     *
     * @param key 변환할 키 문자열
     * @return 접미사 부분을 Long으로 변환한 값
     */
    public static Long parseSuffix(String key) {
        String[] parts = key.split(SEPARATOR_2, 2);
        return Long.valueOf(parts[1].trim());
    }

    /**
     * 결합된 HashKey와 FieldKey를 분리하는 메서드입니다.
     *
     * @param key 결합된 HashKey와 FieldKey 문자열
     * @return 첫 번째 요소가 HashKey, 두 번째 요소가 FieldKey인 String 배열
     */
    public static String[] parseHashKeyAndFieldKey(String key) {
        return key.split(SEPARATOR_3, 2);
    }

}
