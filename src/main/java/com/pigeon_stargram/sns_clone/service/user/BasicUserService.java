package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.follow.Follow;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.response.ResponseFollowerDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.follow.FollowBuilder;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_CACHE_KEY;
import static com.pigeon_stargram.sns_clone.constant.CacheConstants.USER_NAME_TO_ID_MAPPING_CACHE_KEY;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildResponseOnlineStatusDto;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BasicUserService implements UserService {

    private final SimpMessagingTemplate messagingTemplate;

    private final UserRepository userRepository;
    private final RedisService redisService;
    private final FollowCrudService followCrudService;

    public User findById(Long id) {
        String fieldKey = id.toString();

        // 캐시 히트: 캐시에 사용자 정보가 있는 경우
        if (redisService.hasFieldInHash(USER_CACHE_KEY, fieldKey)) {
            log.info("캐시 히트 - 사용자 정보가 캐시에 있습니다. ID: {}", id);
            return redisService.getValueFromHash(USER_CACHE_KEY, fieldKey, User.class);
        }

        // 캐시 미스: 캐시에 사용자 정보가 없는 경우
        log.info("캐시 미스 - 사용자 정보가 캐시에 없습니다. DB에서 사용자 정보를 조회합니다. ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID + "id=" + id));

        // 캐시에 사용자 정보 저장
        redisService.putValueInHash(USER_CACHE_KEY, fieldKey, user);

        return user;
    }
    public User findByName(String name) {
        String fieldKey = name;

        // 캐시 히트: 캐시에 이름 -> ID 매핑이 있는 경우
        if (redisService.hasFieldInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey)) {
            Long userId = redisService.getValueFromHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey, Long.class);
            return findById(userId);
        }

        // 캐시 미스: 캐시에 이름 -> ID 매핑이 없는 경우
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_NAME + "name=" + name));

        // 이름과 ID의 매핑을 캐시에 저장
        redisService.putValueInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey, user.getId());

        // ID로 사용자 정보 저장 (findById를 통해 캐싱도 처리됨)
        return findById(user.getId());
    }

    // 검색과 관련된 정보는 캐시하지 않음 (DB에서 조회)
    @Override
    public List<User> findBySearchQuery(String searchQuery) {
        return userRepository.findByNameContainingIgnoreCase(searchQuery);
    }

    // 로그인과 관련된 정보는 캐시하지 않음 (DB에서 조회)
    public User findByWorkEmail(String email) {
        List<User> findUsers = userRepository.findByWorkEmail(email);
        if (findUsers.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL);
        } else if (findUsers.size() > 1) {
            throw new MultipleUsersFoundException(MULTIPLE_USERS_FOUND_EMAIL);
        }
        return findUsers.getFirst();
    }

    // 로그인과 관련된 정보는 캐시하지 않음 (DB에서 조회)
    public User findByWorkEmailAndPassword(String email, String password) {
        List<User> findUsers = userRepository.findByWorkEmailAndPassword(email, password);
        if (findUsers.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL_PASSWORD);
        } else if (findUsers.size() > 1) {
            throw new MultipleUsersFoundException(MULTIPLE_USERS_FOUND_EMAIL_PASSWORD);
        }
        return findUsers.getFirst();
    }

    public ResponseUserChatDto findUserChatById(Long userId) {
        // id를 통해 캐시된 user 값을 가져옴
        User user = findById(userId);
        // user 값을 채팅을 위한 Dto로 변환
        return buildResponseUserChatDto(user);
    }

    public User save(RequestRegisterDto userDto) {
        try {
            // 사용자 엔티티 생성 및 데이터베이스에 저장
            User savedUser = userRepository.save(userDto.toEntity());

            // Write-through 캐싱: 데이터베이스에 저장된 후 캐시에도 저장
            // ID를 키로 사용자 객체 캐싱
            redisService.putValueInHash(USER_CACHE_KEY, savedUser.getId().toString(), savedUser);
            // 이름을 키로 ID 캐싱
            redisService.putValueInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, savedUser.getName(), savedUser.getId());

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw new RegisterFailException(REGISTER_FAIL_EMAIL, e);
        }
    }


    // TestData에서만 사용
    public List<User> saveAll(List<UserDto> userDtoList) {
        // 1. DTO 리스트를 엔티티 리스트로 변환
        List<User> users = userDtoList.stream()
                .map(UserDto::toEntity)
                .collect(Collectors.toList());

        // 2. 엔티티 리스트를 데이터베이스에 저장
        List<User> savedUsers = userRepository.saveAll(users);

        // 3. 각 사용자 정보를 캐시에 저장 (Write-through 캐싱 전략)
        savedUsers.forEach(user -> {
            redisService.putValueInHash(USER_CACHE_KEY, user.getId().toString(), user);
            redisService.putValueInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, user.getName(), user.getId());
        });

        // 4. 저장된 사용자 리스트 반환
        return savedUsers;
    }


    @Override
    public User updateOnlineStatus(UpdateOnlineStatusDto dto) {
        // 캐시에서 사용자 정보 조회
        User user = findById(dto.getUserId());

        // 사용자의 온라인 상태 업데이트
        user.updateOnlineStatus(dto.getOnlineStatus());

        // 변경된 사용자 정보를 캐시에 업데이트 (Write-through 캐싱 전략)
        redisService.putValueInHash(USER_CACHE_KEY, user.getId().toString(), user);

        // Redis 채널을 통해 다른 서비스에 상태 변경 전파
        redisService.publishMessage("user.online.status", dto);

        // 업데이트된 사용자 정보 반환
        return user;
    }

    public void handleOnlineStatusUpdate(UpdateOnlineStatusDto dto) {
        Long userId = dto.getUserId();
        String onlineStatus = dto.getOnlineStatus();

        List<ResponseFollowerDto> followerDtos = getFollowerDtos(userId);
        List<ResponseFollowerDto> followingDtos = getFollowingDtos(userId);

        Stream.concat(followerDtos.stream(), followingDtos.stream())
                .distinct()
                .map(ResponseFollowerDto::getId)
                .forEach(chatUserId -> {
                    String destination = "/topic/users/status/" + chatUserId;
                    ResponseOnlineStatusDto responseOnlineStatusDto =
                            buildResponseOnlineStatusDto(userId, onlineStatus);
                    messagingTemplate.convertAndSend(destination, responseOnlineStatusDto);
                });
    }

    public List<ResponseFollowerDto> getFollowingDtos(Long userId) {
        return followCrudService.findFollowingIds(userId).stream()
                .map(this::findById)
                .map(FollowBuilder::buildResponseFollowerDto)
                .collect(Collectors.toList());
    }

    public List<ResponseFollowerDto> getFollowerDtos(Long userId) {
        return followCrudService.findFollowerIds(userId).stream()
                .map(this::findById)
                .map(FollowBuilder::buildResponseFollowerDto)
                .collect(Collectors.toList());
    }

    // 로그인과 관련된 정보는 캐시하지 않음 (DB에서 조회)
    @Override
    public User updatePassword(UpdatePasswordDto dto) {
        User user = findById(dto.getUserId());
        user.updatePassword(dto.getPassword());
        return user;
    }

    public ResponseOnlineStatusDto getOnlineStatus(Long id) {
        // 캐시에서 사용자 정보 조회
        User user = findById(id);
        // Dto로 변환
        return buildResponseOnlineStatusDto(user.getId(), user.getOnlineStatus());
    }

    public List<ResponseUserInfoDto> getUserInfosByUserIds(List<Long> userIds) {
        // 캐시에서 사용자 정보 조회(findById)
        // Dto로 변환
        // List로 만들어 return
        return userIds.stream()
                .map(this::findById)
                .map(ResponseUserInfoDto::new)
                .collect(Collectors.toList());
    }
}
