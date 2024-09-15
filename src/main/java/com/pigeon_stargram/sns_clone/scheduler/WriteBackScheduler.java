package com.pigeon_stargram.sns_clone.scheduler;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.chat.implV2.ChatWriteBackService;
import com.pigeon_stargram.sns_clone.service.comment.implV2.CommentWriteBackService;
import com.pigeon_stargram.sns_clone.service.follow.implV2.FollowWriteBackService;
import com.pigeon_stargram.sns_clone.service.post.implV2.PostWriteBackService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.service.reply.implV2.ReplyWriteBackService;
import com.pigeon_stargram.sns_clone.service.search.implV2.SearchWriteBackService;
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

/**
 * 캐시 데이터를 주기적으로 DB에 기록하는 스케줄러 클래스입니다.
 *
 * 이 클래스는 Redis에 저장된 데이터를 특정 간격으로 가져와 데이터베이스에 동기화하며,
 * 필요한 경우 일괄적으로 캐시 데이터를 DB에 기록하는 작업을 수행합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WriteBackScheduler {

    // 정규 표현식과 해당하는 처리 메서드를 매핑하는 맵
    private static final Map<String, Consumer<String>> regexPatterns = new HashMap<>();

    private final PostWriteBackService postWriteBackService;
    private final FollowWriteBackService followWriteBackService;
    private final SearchWriteBackService searchWriteBackService;
    private final CommentWriteBackService commentWriteBackService;
    private final ReplyWriteBackService replyWriteBackService;
    private final ChatWriteBackService chatWriteBackService;
    private final RedisService redisService;

    /**
     * 주어진 키의 데이터를 DB에 기록하는 메서드입니다.
     *
     * @param key 저장할 Redis 키
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

    /**
     * 각 패턴과 해당 Write-Back 메서드를 미리 설정하는 메서드입니다.
     */
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

    /**
     * 일정 간격으로 캐시 데이터를 DB에 동기화하는 스케줄러 메서드입니다.
     *
     * 각 주기마다 Redis Sorted Set에서 데이터를 가져와 DB에 기록합니다.
     */
    @Scheduled(fixedRate = 100)
    public void syncCacheToDB() {
        // 한 번에 가져올 Write-Back 작업의 개수 설정
        Integer writeBackBatchSize = (Integer) redisService.getValue(WRITE_BACK_BATCH_SIZE);
        if (writeBackBatchSize == null) {
            writeBackBatchSize = WRITE_BACK_BATCH_SIZE_INIT;
        }

        // Redis Sorted Set에서 하위 N개의 값을 가져옴
        List<String> sortedSetList =
                redisService.getAndRemoveBottomNFromSortedSet(WRITE_BACK, writeBackBatchSize, String.class);

        if (sortedSetList.size() < writeBackBatchSize) {
            redisService.setValue(WRITE_BACK_BATCH_SIZE, WRITE_BACK_BATCH_SIZE_INIT);
        }

        // 각 가져온 키에 대해 DB 기록 처리
        for (String writeBackKey : sortedSetList) {
            writeBack(writeBackKey); // 각 키에 대해 writeBack 처리
        }
    }

    /**
     * 서버중 "write-back-boost" 프로파일 활성화 된 서버일 경우, 캐시 데이터를 일괄적으로 DB에 기록하는 메서드입니다.
     *
     * 일부 키들의 경우 계속해서 DB에 Flush 되지 않을 수 있기 때문에, Starvation을 막기 위해
     *
     * 주기적으로 Redis Sorted Set의 모든 키에 대해 Write-Back 작업을 처리합니다.
     */
    @Profile("write-back-boost")
    @Scheduled(fixedRate = 10000)
    public void syncAllCache() {
        if (!redisService.hasKey(WRITE_BACK)) {
            return;
        }

        log.info("전체 Write Back Boosting 시작");
        Long writeBackKeyNum = redisService.getSortedSetSize(WRITE_BACK);

        // Write-Back 작업을 나눌 배치 크기를 계산
        Integer writeBackBatchSize = Math.toIntExact(writeBackKeyNum / WRITE_BACK_BATCH_NUM);
        if (writeBackBatchSize < 1) {
            writeBackBatchSize = 1;
        }

        redisService.setValue(WRITE_BACK_BATCH_SIZE, writeBackBatchSize);
    }
}
