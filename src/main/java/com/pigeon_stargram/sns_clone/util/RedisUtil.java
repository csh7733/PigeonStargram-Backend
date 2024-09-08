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

    public static String cacheKeyPatternGenerator(String value,
                                                  String prefix) {
        return value + SEPARATOR_1 + prefix + SEPARATOR_2 + "*";
    }
}
