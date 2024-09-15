package com.pigeon_stargram.sns_clone.constant;

/**
 * Redis에서 업로드중인 게시물과 관련된 상수들을 정의한 클래스입니다.
 */
public class RedisPostConstants {

    // 업로드중인 게시물 ID 상태를 저장하는 Redis 해시 키
    // key : postId의 uuid , value : 실제 postId
    public static final String UPLOADING_POSTS_HASH = "UPLOADING_POSTS_HASH";

    // 업로드 중인 게시물 목록을 저장하는 Redis Set 키
    public static final String UPLOADING_POSTS_SET = "UPLOADING_POSTS_SET";
}
