package com.pigeon_stargram.sns_clone.constant;

/**
 * 캐시에서 사용되는 상수들을 정의한 클래스입니다.
 *
 * 이 클래스는 캐시 키, TTL(Time-To-Live) 설정, 캐시 관련 상수들을 관리합니다.
 */
public class CacheConstants {

    // 데이터 관련 캐시 키
    public static final String POST = "post";
    public static final String COMMENT = "comment";
    public static final String REPLY = "reply";
    public static final String STORY = "STORY";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_CONTENT = "notificationContent";

    public static final String ALL_POST_IDS = "allPostIds";
    public static final String ALL_COMMENT_IDS = "allCommentIds";
    public static final String ALL_REPLY_IDS = "allReplyIds";

    public static final String RECENT_POST_IDS = "recentPostIds";
    public static final String RECENT_COMMENT_IDS = "recentCommentIds";
    public static final String RECENT_REPLY_IDS = "recentReplyIds";

    public static final String POST_LIKE_USER_IDS = "postLikeUserIds";
    public static final String COMMENT_LIKE_USER_IDS = "commentLikeUserIds";
    public static final String REPLY_LIKE_USER_IDS = "replyLikeUserIds";

    public static final String FOLLOWER_IDS = "followerIds";
    public static final String FOLLOWING_IDS = "followingIds";
    public static final String NOTIFICATION_ENABLED_IDS = "notificationEnabledIds";

    public static final String NOTIFICATION_IDS = "notificationIds";
    public static final String NOTIFICATION_CONTENT_IDS = "notificationContentIds";

    public static final String UNREAD_CHAT_COUNT = "unreadChatCount";
    public static final String LAST_MESSAGE = "lastMessage";
    public static final String STORY_VIEWS = "storyViews";
    public static final String USER_STORIES = "userStories";
    public static final String SEARCH_HISTORY = "searchHistory";
    public static final String SEARCH_TERM_SCORES = "searchTermScores";
    public static final String USER_CACHE_KEY = "userCacheKey";
    public static final String USER_NAME_TO_ID_MAPPING_CACHE_KEY = "userNameToIdMapping";
    public static final String TIMELINE = "timeline";

    // WRITE BACK 처리 관련 상수
    public static final String WRITE_BACK = "writeBack";
    public static final String WRITE_BACK_BATCH_SIZE = "writeBackBatchSize";

    // 키 관련 상수
    public static final String USER_ID = "userId";
    public static final String POST_ID = "postId";
    public static final String COMMENT_ID = "commentId";
    public static final String REPLY_ID = "replyId";
    public static final String STORY_ID = "storyId";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION_CONTENT_ID = "notificationContentId";

    // TTL 설정 (분 단위)
    public static final Long ONE_DAY_TTL = 1440L;
    public static final Long ONE_HOUR_TTL = 60L;
    public static final Long ONE_MINUTE_TTL = 1L;

    // 키 생성 시 사용할 구분자
    public static final String SEPARATOR_1 = "::";
    public static final String SEPARATOR_2 = "_";
    public static final String SEPARATOR_3 = "#";

    // 캐시 관련 설정
    public static final Integer WRITE_BACK_BATCH_NUM = 10;
    public static final Integer WRITE_BACK_BATCH_SIZE_INIT = 3;

}
