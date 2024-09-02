package com.pigeon_stargram.sns_clone.util;

import org.springframework.stereotype.Component;

public class RedisUtil {

    public static String cacheKeyGenerator(String value,
                                           String prefix,
                                           String suffix) {
        return value + "::" + prefix + "_" + suffix;
    }

    public static String cacheKeyGenerator(String value,
                                           String prefix) {
        return value + "::" + prefix;
    }
}
