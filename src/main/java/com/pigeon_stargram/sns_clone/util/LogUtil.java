package com.pigeon_stargram.sns_clone.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 로그를 쉽게 생성하도록 도와주는 유틸클래스
 */
@Slf4j
public class LogUtil {

    /**
     * Controller 계층의 메서드에서 호출하는 로그 메서드
     *
     * @param methodName 호출한 메서드의 이름
     * @param param      호출한 메서드의 파라미터 정보
     */
    public static void logControllerMethod(String methodName,
                                           Object... param) {
        log.info("CONTROLLER: Method={}, Parameter={}", methodName, Arrays.toString(param));
    }

    /**
     * Service 계층의 메서드에서 호출하는 로그 메서드
     *
     * @param methodName 호출한 메서드의 이름
     * @param param      호출한 메서드의 파라미터 정보
     */
    public static void logServiceMethod(String methodName,
                                        Object... param) {
        log.info("SERVICE: Method={}, Parameter={}", methodName, Arrays.toString(param));
    }

    /**
     * 캐시에 Key가 있을때 호출하는 로그 메서드
     * @param key   캐시 Key
     * @param value 캐시 Value
     */
    public static void logCacheHit(String key,
                                   Object value) {
        log.info("CACHE HIT: Key={}, Value={}", key, value);
    }

    /**
     * 캐시에 Key가 없을때 호출하는 로그 메서드
     * @param key   캐시 Key
     */
    public static void logCacheMiss(String key) {
        log.info("CACHE MISS: Key={}", key);
    }

}
