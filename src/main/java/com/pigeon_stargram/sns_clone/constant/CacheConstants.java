package com.pigeon_stargram.sns_clone.constant;

public class CacheConstants {

    // data
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


    // key
    public static final String USER_ID = "userId";
    public static final String POST_ID = "postId";
    public static final String COMMENT_ID = "commentId";
    public static final String REPLY_ID = "replyId";
    public static final String STORY_ID = "storyId";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION_CONTENT_ID = "notificationContentId";


    // TTL
    public static final Long ONE_DAY_TTL = 1440L;
    public static final Long ONE_HOUR_TTL = 60L;
    public static final Long ONE_MINUTE_TTL = 1L;
}
