package com.pigeon_stargram.sns_clone.util;

import org.springframework.stereotype.Component;

public class RedisUtil {

    public static String cacheKeyGenerator(String prefix, String suffix) {
        return prefix + "_" + suffix;
    }
}
