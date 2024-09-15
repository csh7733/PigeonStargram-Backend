package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestData {
    private final UserService userService;
    private final FollowService followService;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initData() throws IOException {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        initData1();
        initData2();
    }

    public void initData1() throws IOException {
        List<RequestRegisterDto> testData = List.of(
                new RequestRegisterDto("test1@gmil.com", "test", "PigeonKing", "Pigeon University", "010-1234-5678", "avatar-1.png"),
                new RequestRegisterDto("test2@gmil.com", "test", "ChickMaster", "Chicken Academy", "010-2234-5678", "avatar-2.png"),
                new RequestRegisterDto("test3@gmil.com", "test", "WingedHero", "Pigeon Institute of Technology", "010-3234-5678", "avatar-3.png"),
                new RequestRegisterDto("test4@gmil.com", "test", "RoosterLeader", "Chicken University", "010-4234-5678", "avatar-4.png"),
                new RequestRegisterDto("test5@gmil.com", "test", "FeatherQueen", "Pigeon Science Academy", "010-5234-5678", "avatar-5.png"),
                new RequestRegisterDto("test6@gmil.com", "test", "EggGuardian", "Chicken State University", "010-6234-5678", "avatar-6.png"),
                new RequestRegisterDto("test7@gmil.com", "test", "SkyWatcher", "Pigeon College of Aviation", "010-7234-5678", "avatar-7.png"),
                new RequestRegisterDto("test8@gmil.com", "test", "NestProtector", "Chicken Technical University", "010-8234-5678", "avatar-1.png"),
                new RequestRegisterDto("test9@gmil.com", "test", "CluckCommander", "Chicken Institute", "010-9234-5678", "avatar-2.png"),
                new RequestRegisterDto("test10@gmil.com", "test", "DoveChampion", "Pigeon College of Arts", "010-0234-5678", "avatar-3.png"),
                new RequestRegisterDto("test11@gmil.com", "test", "HatchMaster", "Chicken Innovation University", "010-1234-5679", "avatar-4.png"),
                new RequestRegisterDto("test12@gmil.com", "test", "FlightLord", "Pigeon Wing Academy", "010-2234-5679", "avatar-5.png"),
                new RequestRegisterDto("test13@gmil.com", "test", "ChickKing", "Chicken Kingdom University", "010-3234-5679", "avatar-6.png"),
                new RequestRegisterDto("test14@gmil.com", "test", "FalconRider", "Pigeon Engineering College", "010-4234-5679", "avatar-7.png"),
                new RequestRegisterDto("test15@gmil.com", "test", "HenQueen", "Chicken University of Science", "010-5234-5679", "avatar-1.png")
        );
        testData.forEach(userService::save);
    }

    public void initData2() {
        followService.createFollow(toAddFollowDto(1L, 2L));
        followService.createFollow(toAddFollowDto(1L, 3L));
        followService.createFollow(toAddFollowDto(1L, 4L));
        followService.createFollow(toAddFollowDto(1L, 5L));
        followService.createFollow(toAddFollowDto(2L, 6L));
        followService.createFollow(toAddFollowDto(2L, 7L));
        followService.createFollow(toAddFollowDto(2L, 8L));
        followService.createFollow(toAddFollowDto(2L, 9L));
        followService.createFollow(toAddFollowDto(3L, 10L));
        followService.createFollow(toAddFollowDto(3L, 11L));
        followService.createFollow(toAddFollowDto(3L, 12L));
        followService.createFollow(toAddFollowDto(3L, 13L));
        followService.createFollow(toAddFollowDto(4L, 14L));
        followService.createFollow(toAddFollowDto(4L, 15L));
        followService.createFollow(toAddFollowDto(5L, 1L));
        followService.createFollow(toAddFollowDto(5L, 6L));
        followService.createFollow(toAddFollowDto(6L, 2L));
        followService.createFollow(toAddFollowDto(6L, 3L));
        followService.createFollow(toAddFollowDto(7L, 4L));
        followService.createFollow(toAddFollowDto(7L, 5L));
        followService.createFollow(toAddFollowDto(8L, 7L));
        followService.createFollow(toAddFollowDto(8L, 8L));
        followService.createFollow(toAddFollowDto(9L, 9L));
        followService.createFollow(toAddFollowDto(10L, 10L));
        followService.createFollow(toAddFollowDto(12L, 12L));
        followService.createFollow(toAddFollowDto(13L, 13L));
        followService.createFollow(toAddFollowDto(14L, 14L));
        followService.createFollow(toAddFollowDto(15L, 15L));
        followService.createFollow(toAddFollowDto(1L, 6L));
        followService.createFollow(toAddFollowDto(2L, 10L));
        followService.createFollow(toAddFollowDto(3L, 7L));
        followService.createFollow(toAddFollowDto(4L, 8L));
        followService.createFollow(toAddFollowDto(5L, 9L));
        followService.createFollow(toAddFollowDto(6L, 11L));
        followService.createFollow(toAddFollowDto(7L, 12L));
        followService.createFollow(toAddFollowDto(8L, 13L));
        followService.createFollow(toAddFollowDto(9L, 14L));
        followService.createFollow(toAddFollowDto(10L, 15L));
        followService.createFollow(toAddFollowDto(11L, 1L));
        followService.createFollow(toAddFollowDto(12L, 2L));
        followService.createFollow(toAddFollowDto(13L, 3L));
        followService.createFollow(toAddFollowDto(14L, 4L));
        followService.createFollow(toAddFollowDto(15L, 5L));
        followService.createFollow(toAddFollowDto(1L, 11L));
        followService.createFollow(toAddFollowDto(2L, 12L));
        followService.createFollow(toAddFollowDto(5L, 15L));
        followService.createFollow(toAddFollowDto(6L, 1L));
        followService.createFollow(toAddFollowDto(7L, 2L));
        followService.createFollow(toAddFollowDto(8L, 3L));
        followService.createFollow(toAddFollowDto(9L, 4L));
        followService.createFollow(toAddFollowDto(10L, 5L));
        followService.createFollow(toAddFollowDto(11L, 6L));
        followService.createFollow(toAddFollowDto(11L, 3L));
        followService.createFollow(toAddFollowDto(12L, 7L));
        followService.createFollow(toAddFollowDto(13L, 8L));
        followService.createFollow(toAddFollowDto(14L, 9L));
        followService.createFollow(toAddFollowDto(15L, 10L));
        followService.createFollow(toAddFollowDto(1L, 7L));
        followService.createFollow(toAddFollowDto(2L, 14L));
        followService.createFollow(toAddFollowDto(3L, 15L));
        followService.createFollow(toAddFollowDto(4L, 11L));
        followService.createFollow(toAddFollowDto(5L, 12L));
        followService.createFollow(toAddFollowDto(6L, 13L));
    }

}
