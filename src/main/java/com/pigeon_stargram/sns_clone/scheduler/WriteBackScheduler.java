package com.pigeon_stargram.sns_clone.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WriteBackScheduler {

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    public void syncCacheToDB() {

    }
}
