package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.RequestAddFollowerDto;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {

    private final UserService userService;
    private final FollowRepository followRepository;

    public Follow follow(AddFollowerDto dto) {
        User fromUser = userService.findById(dto.getFromId());
        User toUser = userService.findById(dto.getToId());
        return followRepository.save(dto.toEntity(fromUser, toUser));
    }

    public List<User> findFollowers(User user) {
        List<Follow> followList = followRepository.findByToUser(user);
        return followList.stream()
                .map(Follow::getFromUser)
                .collect(Collectors.toList());
    }

    public List<User> findFollowings(User user) {
        List<Follow> followList = followRepository.findByFromUser(user);
        return followList.stream()
                .map(Follow::getToUser)
                .collect(Collectors.toList());
    }

    public List<Follow> saveAll(List<FollowerDto> followerDtoList) {
        List<User> users = followerDtoList.stream()
                .map(FollowerDto::toUser)
                .collect(Collectors.toList());
        userService.saveAllUser(users);

        List<Follow> follows = followerDtoList.stream()
                .map(dto -> dto.toEntity(userService.findById(dto.getId())))
                .collect(Collectors.toList());
        return followRepository.saveAll(follows);
    }

    public List<User> findAll(){
        return followRepository.findAll().stream()
                .map(Follow::getToUser)
                .collect(Collectors.toList());
    }
}
