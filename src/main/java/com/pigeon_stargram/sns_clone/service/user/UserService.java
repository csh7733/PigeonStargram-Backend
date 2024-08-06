package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.FilterFollowersDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> saveAll(List<UserDto> userDtoList) {
        List<User> users = userDtoList.stream()
                .map(UserDto::toEntity)
                .collect(Collectors.toList());
        return userRepository.saveAll(users);
    }

    public List<User> saveAllUser(List<User> users) {
        return userRepository.saveAll(users);
    }

    public List<User> findFollowersByFilter(FilterFollowersDto dto) {
        return userRepository.findFollowersByFilter(dto.getUserId(), dto.getKey());
    }
}
