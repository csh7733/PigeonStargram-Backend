package com.pigeon_stargram.sns_clone.constant;

/**
 * 팔로우 기능과 관련된 상수들을 정의한 클래스입니다.
 *
 * 이 클래스는 팔로우 상태, 팔로우 유저 목록, 유명 유저 기준 등을 관리합니다.
 */
public class FollowConstants {

    // 채팅 가능한 유저 상태에 대한 상수
    public static final Integer BOTH_FOLLOWING = 2;   // 서로 팔로우한 상태
    public static final Integer FOLLOWING_ONLY = 1;   // 내가 팔로우하고 있는 상태
    public static final Integer FOLLOWED_ONLY = 0;    // 상대방이 나를 팔로우한 상태

    // 팔로우 유저 목록에서 팔로우 상태에 대한 상수
    public static final Integer FOLLOWING = 1;        // 팔로우 중인 상태
    public static final Integer NOT_FOLLOWING = 2;    // 팔로우하지 않은 상태

    // 유명 유저 기준 팔로워 수 임계값
    public static final Integer FAMOUS_USER_THRESHOLD = 5;
}
