package com.pigeon_stargram.sns_clone.config.redis;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component("CacheKeyGenerator")
public class CacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append(method.getName());
        for (Object param : params) {
            keyBuilder.append("_").append(param);
        }

        return keyBuilder.toString();
    }
}
