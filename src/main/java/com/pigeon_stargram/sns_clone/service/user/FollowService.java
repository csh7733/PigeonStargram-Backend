package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.user.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {

    private final FollowRepository followRepository;

    public Follow save(Follow follow) {
        return followRepository.save(follow);
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
}
