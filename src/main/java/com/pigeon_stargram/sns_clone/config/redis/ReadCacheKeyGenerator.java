package com.pigeon_stargram.sns_clone.config.redis;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component("ReadCacheKeyGenerator")
public class ReadCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target,
                           Method method,
                           Object... params) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append(method.getName());
        for (Object param : params) {
            keyBuilder.append("_").append(param);
        }

        return keyBuilder.toString();
    }

    public Object generate(String valueName,
                           String methodName,
                           List<Object> params) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append(valueName);
        keyBuilder.append(methodName);
        for (Object param : params) {
            keyBuilder.append("_").append(param);
        }

        return keyBuilder.toString();
    }
}
