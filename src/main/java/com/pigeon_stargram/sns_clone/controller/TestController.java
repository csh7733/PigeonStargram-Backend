package com.pigeon_stargram.sns_clone.controller;

import com.pigeon_stargram.sns_clone.config.auth.annotation.LoginUser;
import com.pigeon_stargram.sns_clone.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/test")
@RestController
public class TestController {

    @GetMapping("/not-login")
    public ResponseEntity<String> simpleTest() {
        return ResponseEntity.ok("Not login Test Success");
    }

    @GetMapping("/login")
    public ResponseEntity<String> simple2Test(@LoginUser SessionUser loginUser) {
        return ResponseEntity.ok(loginUser.getName());
    }

    @GetMapping("/cpu-intensive")
    public ResponseEntity<String> cpuIntensiveTest() {
        long sum = 0;
        for (long i = 0; i < 1000000000L; i++) {
            sum += i;
        }
        return ResponseEntity.ok("CPU Intensive Test Finished: " + sum);
    }

    @GetMapping("/io-wait")
    public ResponseEntity<String> ioWaitTest() throws InterruptedException {
        Thread.sleep(5000); // 5초 지연
        return ResponseEntity.ok("I/O Wait Test Finished");
    }
}
