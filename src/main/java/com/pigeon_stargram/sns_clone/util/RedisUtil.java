package com.pigeon_stargram.sns_clone.util;

import com.pigeon_stargram.sns_clone.constant.CacheConstants;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;

public class RedisUtil {

    public static String cacheKeyGenerator(String value,
                                           String prefix,
                                           String suffix) {
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + suffix;
    }

    public static String cacheKeyGenerator(String value,
                                           String prefix) {
        return value + SEPARATOR_1 + prefix;
    }

    public static String cacheKeyPatternGenerator(String value,
                                                  String prefix) {
        // 숫자가 하나 이상 포함된 것
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + ".+";
    }

    /**
     * Dirty Hash를 WriteBack Set에 추가할 수 있게 HashKey와 Field를 합친다.
     * @param hashKey Dirty HashKey
     * @param fieldKey Dirty FieldKey
     * @return 합쳐진 문자열
     */
    public static String hashWriteBackKeyGenerator(String hashKey,
                                                   String fieldKey) {
        return hashKey + SEPARATOR_3 + fieldKey;
    }

    public static Long parseSuffix(String key) {
        String[] parts = key.split(SEPARATOR_2, 2);
        return Long.valueOf(parts[1].trim());
    }

    /**
     * hashWriteBackKeyGenerator 메서드로 합쳐진 요소를 hashKey와 fieldKey로 나눈다.
     * @param key 합쳐친 문자열
     * @return 첫번째 요소가 HashKey, 두번째 요소가 FieldKey 인 String 배열
     */
    public static String[] parseHashKeyAndFieldKey(String key) {
        return key.split(SEPARATOR_3, 2);
    }

}
