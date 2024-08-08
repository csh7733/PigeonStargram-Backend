package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.DeleteFollowDto;
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

    public Follow createFollow(AddFollowDto dto) {
        User fromUser = userService.findById(dto.getFromId());
        User toUser = userService.findById(dto.getToId());
        followRepository.findByFromUserAndToUser(fromUser, toUser)
                .ifPresent(follow -> {
                    throw new IllegalArgumentException("이미 팔로우 중입니다.");
                });
        return followRepository.save(dto.toEntity(fromUser, toUser));
    }

    public void deleteFollow(DeleteFollowDto dto){
        User fromUser = userService.findById(dto.getFromId());
        User toUser = userService.findById(dto.getToId());
        followRepository.findByFromUserAndToUser(fromUser, toUser)
                .ifPresent(followRepository::delete);
    }

    public List<User> findFollowers(Long userId) {
        User user = userService.findById(userId);
        return followRepository.findByToUser(user).stream()
                .map(Follow::getFromUser)
                .collect(Collectors.toList());
    }

    public List<User> findFollowings(Long userId) {
        User user = userService.findById(userId);
        return followRepository.findByFromUser(user).stream()
                .map(Follow::getToUser)
                .collect(Collectors.toList());
    }

    public List<User> findAll(){
        return followRepository.findAll().stream()
                .map(Follow::getToUser)
                .collect(Collectors.toList());
    }
}
