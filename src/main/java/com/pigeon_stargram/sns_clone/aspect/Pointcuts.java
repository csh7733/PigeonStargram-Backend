package com.pigeon_stargram.sns_clone.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Pointcuts {

    @Pointcut("execution(* com.pigeon_stargram.sns_clone" +
            ".service.follow.FollowService.createFollow(..))")
    public void addFollow(){}
}
