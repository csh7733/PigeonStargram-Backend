package com.pigeon_stargram.sns_clone.util;

public class RedisUtil {

    private static final String SEPARATOR_1 = "::";
    private static final String SEPARATOR_2 = "_";


    public static String cacheKeyGenerator(String value,
                                           String prefix,
                                           String suffix) {
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + suffix;
    }

    public static String cacheKeyGenerator(String value,
                                           String prefix) {
        return value + SEPARATOR_1 + prefix;
    }

    public static String cacheKeyWildcardPatternGenerator(String value,
                                                          String prefix) {
        // Redis의 와일드카드 패턴에서는 숫자를 의미하는 표현이 없음.
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + "*";
    }

    public static String cacheKeyRegexPatternGenerator(String value,
                                                       String prefix) {
        // 숫자가 하나 이상 포함된 것
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + "\\d+";
    }
}
