package com.pigeon_stargram.sns_clone.constant;

/**
 * Redis에서 사용자 관련 데이터를 관리하기 위한 상수들을 정의한 클래스입니다.
 *
 * 이 클래스는 접속중인 사용자와 세션 사용자 매핑과 관련된 Redis 키를 정의합니다.
 */
public class RedisUserConstants {

    // 접속중인 사용자를 관리하기 위한 Redis 키의 접두사
    public static final String ACTIVE_USERS_KEY_PREFIX = "activeUsers:";

    // 세션과 사용자 정보를 매핑하기 위한 Redis 키
    public static final String SESSION_USER_MAP_KEY = "sessionUserMap";
}
