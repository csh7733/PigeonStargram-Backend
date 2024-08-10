package com.pigeon_stargram.sns_clone.aspect;

import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class NotificationAspect {

//    private final NotificationService notificationService;

    @After("com.pigeon_stargram.sns_clone" +
            ".aspect.Pointcuts.addFollow()")
    public void notifyAddFollow(JoinPoint joinPoint) {
        AddFollowDto dto = joinPointToDto(joinPoint);
        log.info("dto={}", dto);
    }

    public <T> T joinPointToDto(JoinPoint joinPoint) {
        return (T) joinPoint.getArgs()[0];
    }
}
