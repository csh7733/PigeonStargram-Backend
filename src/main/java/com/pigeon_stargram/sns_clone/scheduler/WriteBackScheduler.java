package com.pigeon_stargram.sns_clone.scheduler;

import com.pigeon_stargram.sns_clone.service.post.PostWriteBackService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WriteBackScheduler {

    private final PostWriteBackService postWriteBackService;

    // 10초마다 실행
    @Scheduled(fixedRate = 20000)
    public void syncCacheToDB() {
        postWriteBackService.writeBackPostLikeUserIds();
    }
}
