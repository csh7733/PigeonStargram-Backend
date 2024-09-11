package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.util.RedisUtil.cacheKeyPatternGenerator;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class WriteBackScheduler {

    private final PostWriteBackService postWriteBackService;

    private final RedisService redisService;

    private static final Map<String, Consumer<String>> regexPatterns = new HashMap<>();

    @PostConstruct
    public void prepareRegexPatterns() {
        regexPatterns.put(
                cacheKeyPatternGenerator(POST_LIKE_USER_IDS, POST_ID),
                postWriteBackService::syncPostLikeUserIds);
    }

    @Scheduled(fixedRate = 10000)
    public void syncCacheToDB() {

        Object task = redisService.popFromSet(WRITE_BACK);
        if(task == null){
            return;
        }

        String writeBackKey = (String) task;
        log.info("WriteBack Set에서 DB에 기록할 Key를 가져왔습니다. key={}", writeBackKey);

        regexPatterns.entrySet().stream()
                .filter(entry -> {
                    String regexPattern = entry.getKey();
                    return writeBackKey.matches(regexPattern);
                })
                .findFirst()
                .ifPresentOrElse(entry -> {
                    Consumer<String> writeBackMethod = entry.getValue();
                    writeBackMethod.accept(writeBackKey);
                }, () -> {
                    throw new PatternNotMatchException(ExceptionMessageConst.PATTERN_NOT_MATCH);
                });
    }


}
