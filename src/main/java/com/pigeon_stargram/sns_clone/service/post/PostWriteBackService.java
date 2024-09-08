package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostWriteBackService {

    private final RedisService redisService;
    private final PostCrudService postCrudService;

    public void writeBackPost() {

    }
}
