package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.constant.RedisPostConstants;
import com.pigeon_stargram.sns_clone.constant.RedisReplyConstants;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import com.pigeon_stargram.sns_clone.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pigeon_stargram.sns_clone.constant.RedisReplyConstants.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.REPLY_NOT_FOUND_ID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReplyCrudService {

    private final ReplyRepository repository;

    @Cacheable(value = "reply",
            key = "#replyId")
    public Reply findById(Long replyId) {
        log.info("findById={}", replyId);
        return repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
    }

    public List<Reply> findByCommentId(Long commentId) {
        return repository.findByCommentId(commentId);
    }

    public Reply save(Reply reply) {
        return repository.save(reply);
    }

    public void deleteById(Long replyId) {
        repository.deleteById(replyId);
    }

    public void deleteAllByCommentId(Long commentId) {
        repository.deleteAllByCommentId(commentId);
    }

}
