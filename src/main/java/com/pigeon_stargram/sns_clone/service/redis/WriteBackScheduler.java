package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.chat.ChatWriteBackService;
import com.pigeon_stargram.sns_clone.service.comment.CommentWriteBackService;
import com.pigeon_stargram.sns_clone.service.follow.FollowWriteBackService;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyWriteBackService;
import com.pigeon_stargram.sns_clone.service.search.SearchWriteBackService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyPatternGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class WriteBackScheduler {

    private static final Map<String, Consumer<String>> regexPatterns = new HashMap<>();

    private final PostWriteBackService postWriteBackService;
    private final FollowWriteBackService followWriteBackService;
    private final SearchWriteBackService searchWriteBackService;
    private final CommentWriteBackService commentWriteBackService;
    private final ReplyWriteBackService replyWriteBackService;
    private final ChatWriteBackService chatWriteBackService;
    private final RedisService redisService;

    /**
     * Key의 데이터를 DB에 기록한다.
     *
     * @param key 저장할 Key
     */
    private static void writeBack(String key) {
        regexPatterns.entrySet().stream()
                .filter(entry -> {
                    String regexPattern = entry.getKey();
                    return key.matches(regexPattern);
                })
                .findFirst()
                .ifPresentOrElse(entry -> {
                    Consumer<String> writeBackMethod = entry.getValue();
                    writeBackMethod.accept(key);
                }, () -> {
                    throw new PatternNotMatchException(ExceptionMessageConst.PATTERN_NOT_MATCH);
                });
    }

    @PostConstruct
    public void prepareRegexPatterns() {
        regexPatterns.put(
                cacheKeyPatternGenerator(POST_LIKE_USER_IDS, POST_ID),
                postWriteBackService::syncPostLikeUserIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(FOLLOWING_IDS, USER_ID),
                followWriteBackService::syncFollowingIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(FOLLOWER_IDS, USER_ID),
                followWriteBackService::syncFollowerIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(NOTIFICATION_ENABLED_IDS, USER_ID),
                followWriteBackService::syncNotificationEnabledIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(SEARCH_HISTORY, USER_ID),
                searchWriteBackService::syncSearchHistory);
        regexPatterns.put(
                cacheKeyPatternGenerator(COMMENT_LIKE_USER_IDS, COMMENT_ID),
                commentWriteBackService::syncCommentLikeUserIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(REPLY_LIKE_USER_IDS, REPLY_ID),
                replyWriteBackService::syncReplyLikeUserIds);
        regexPatterns.put(
                cacheKeyPatternGenerator(UNREAD_CHAT_COUNT, USER_ID),
                chatWriteBackService::syncUnreadChatCount);
        regexPatterns.put(
                cacheKeyPatternGenerator(LAST_MESSAGE, USER_ID),
                chatWriteBackService::syncLastMessage);

        redisService.setValue(WRITE_BACK_BATCH_SIZE, WRITE_BACK_BATCH_SIZE_INIT);
    }

    @Scheduled(fixedRate = 10000)
    public void syncCacheToDB() {
        // 한 번에 몇개를 가져올지 확인
        Integer writeBackBatchSize = (Integer) redisService.getValue(WRITE_BACK_BATCH_SIZE);

        log.info("writeBackBatchSize={}", writeBackBatchSize);
        // 하위 N개의 값을 가져옴
        List<String> sortedSetList =
                redisService.getAndRemoveBottomNFromSortedSet(WRITE_BACK, writeBackBatchSize, String.class);

        // 리스트가 비어 있으면 반환
        if (sortedSetList.size() < writeBackBatchSize) {
            redisService.setValue(WRITE_BACK_BATCH_SIZE, WRITE_BACK_BATCH_SIZE_INIT);
            log.info("Write Back Batch Size를 {}로 설정했습니다.", WRITE_BACK_BATCH_SIZE_INIT);
        }

        // 가져온 모든 키에 대해 처리
        for (String writeBackKey : sortedSetList) {
            log.info("WriteBack Set에서 DB에 기록할 Key를 가져왔습니다. key={}", writeBackKey);
            writeBack(writeBackKey); // 각 키에 대해 writeBack 처리
        }
    }

    @Profile("write-back-boost")
    @Scheduled(fixedRate = 10000)
    public void syncAllCache() {
        log.info("전체 Write Back Boosting 시작");
        Long writeBackKeyNum = redisService.getSortedSetSize(WRITE_BACK);

        Integer writeBackBatchSize = Math.toIntExact(writeBackKeyNum / WRITE_BACK_BATCH_NUM);
        if (writeBackBatchSize < 1) {
            writeBackBatchSize = 1;
        }

        redisService.setValue(WRITE_BACK_BATCH_SIZE, writeBackBatchSize);
        log.info("Write Back Batch Size를 {}로 설정했습니다.", writeBackBatchSize);
    }
}
