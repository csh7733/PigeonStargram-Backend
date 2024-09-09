package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_ID;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_LIKE_USER_IDS;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyRegexPatternGenerator;

@RequiredArgsConstructor
@Service
public class WriteBackScheduler {

    private final PostWriteBackService postWriteBackService;

    private static final Map<String, Consumer<String>> regexPatterns = new HashMap<>();

    @PostConstruct
    public void prepareRegexPatterns() {
        regexPatterns.put(
                cacheKeyRegexPatternGenerator(POST_LIKE_USER_IDS, POST_ID),
                postWriteBackService::syncPostLikeUserIds);
    }

    @Scheduled(fixedRate = 10000)
    public void syncCacheToDB() {

        // set에서 key 가져오기

//        regexPatterns.entrySet().stream()
//                .filter(entry -> {
//                    String regexPattern = entry.getKey();
//                    return expiredKey.matches(regexPattern);
//                })
//                .findFirst()
//                .ifPresentOrElse(entry -> {
//                    Consumer<String> writeBackMethod = entry.getValue();
//                    writeBackMethod.accept(expiredKey);
//                }, () -> {
//                    throw new PatternNotMatchException(ExceptionMessageConst.PATTERN_NOT_MATCH);
//                });

        postWriteBackService.writeBackPostLikeUserIds();
    }

    // add 메서드 중복 확인 ttl 연장
}
