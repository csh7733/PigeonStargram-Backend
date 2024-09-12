package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.follow.FollowWriteBackService;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import com.pigeon_stargram.sns_clone.service.search.SearchWriteBackService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyGenerator;
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
    private final RedisService redisService;

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
    }

    @Scheduled(fixedRate = 100)
    public void syncCacheToDB() {

        List<String> bottomNFromSortedSet = redisService.getAndRemoveBottomNFromSortedSet(WRITE_BACK, 1, String.class);
        if(bottomNFromSortedSet.isEmpty()){
            return;
        }
        String writeBackKey = bottomNFromSortedSet.getFirst();
        log.info("WriteBack Set에서 DB에 기록할 Key를 가져왔습니다. key={}", writeBackKey);

        writeBack(writeBackKey);
    }

    /**
     * Hash 이외의 자료구조를 WriteBack 한다.
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
}
