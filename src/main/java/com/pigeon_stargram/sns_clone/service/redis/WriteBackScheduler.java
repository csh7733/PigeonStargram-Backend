package com.pigeon_stargram.sns_clone.service.redis;

import com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst;
import com.pigeon_stargram.sns_clone.exception.redis.PatternNotMatchException;
import com.pigeon_stargram.sns_clone.service.chat.ChatWriteBackService;
import com.pigeon_stargram.sns_clone.service.comment.CommentWriteBackService;
import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyWriteBackService;
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
    private final CommentWriteBackService commentWriteBackService;
    private final ReplyWriteBackService replyWriteBackService;
    private final ChatWriteBackService chatWriteBackService;
    private final RedisService redisService;

    @PostConstruct
    public void prepareRegexPatterns() {
        regexPatterns.put(
                cacheKeyPatternGenerator(POST_LIKE_USER_IDS, POST_ID),
                postWriteBackService::syncPostLikeUserIds);
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
    }

    @Scheduled(fixedRate = 10000)
    public void syncCacheToDB() {

        List<String> sortedSetList = redisService.getBottomNFromSortedSet(WRITE_BACK, 1, String.class);
        if(sortedSetList.isEmpty()){
            return;
        }
        String writeBackKey = sortedSetList.getFirst();
        log.info("WriteBack Set에서 DB에 기록할 Key를 가져왔습니다. key={}", writeBackKey);

        writeBack(writeBackKey);
        redisService.removeFromSortedSet(WRITE_BACK, writeBackKey);
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
