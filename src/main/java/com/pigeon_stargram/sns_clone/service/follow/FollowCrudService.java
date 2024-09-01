package com.pigeon_stargram.sns_clone.service.follow;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.follow.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FollowCrudService {

    private final FollowRepository repository;

    @Cacheable(value = FOLLOWER_IDS,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #userId")
    public List<Long> findFollowers(Long userId) {
        return repository.findByRecipientId(userId).stream()
                .map(Follow::getSender)
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Cacheable(value = FOLLOWING_IDS,
            key = "T(com.pigeon_stargram.sns_clone.constant.CacheConstants).USER_ID + '_' + #userId")
    public List<Long> findFollowings(Long userId) {
        return repository.findBySenderId(userId).stream()
                .map(Follow::getRecipient)
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
