package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_ID;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.POST_LIKE_USER_IDS;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyRegexPatternGenerator;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisExpiredEventListener implements MessageListener {

    private final PostWriteBackService postWriteBackService;

    private static final Map<String, Consumer<String>> regexPatterns = new HashMap<>();

    @PostConstruct
    public void prepareRegexPatterns() {
        regexPatterns.put(
                cacheKeyRegexPatternGenerator(POST_LIKE_USER_IDS, POST_ID),
                postWriteBackService::syncPostLikeUserIds);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();

        log.info("Key Expired={}", expiredKey);

        regexPatterns.entrySet().stream()
                .filter(entry -> {
                    String regexPattern = entry.getKey();
                    return expiredKey.matches(regexPattern);
                })
                .findFirst()
                .ifPresentOrElse(entry -> {
                    Consumer<String> writeBackMethod = entry.getValue();
                    writeBackMethod.accept(expiredKey);
                }, () -> {
                    throw new PatternNotMatchException(ExceptionMessageConst.PATTERN_NOT_MATCH);
                });
    }
}
