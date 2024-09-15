package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.domain.user.UserFactory;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.follow.FollowCrudService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.constant.CacheConstants.*;
import static com.pigeon_stargram.sns_clone.domain.user.UserFactory.*;
import static com.pigeon_stargram.sns_clone.dto.user.UserDtoConverter.*;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;


// 사용자 정보에 대한 캐싱을 적용한 UserService 구현체
// Value  | Structure | Key                               | FieldKey
// -----  | --------- | --------------------------------- | --------
// user   | Hash      | USER_CACHE_KEY                    | userId
// userId | Hash      | USER_NAME_TO_ID_MAPPING_CACHE_KEY | userName
@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

    private final RedisService redisService;
    private final FollowCrudService followCrudService;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long id) {
        String fieldKey = id.toString();

        // 캐시 히트: 캐시에 사용자 정보가 있는 경우
        if (redisService.hasFieldInHash(USER_CACHE_KEY, fieldKey)) {
            return redisService.getValueFromHash(USER_CACHE_KEY, fieldKey, User.class);
        }

        // 캐시 미스: 캐시에 사용자 정보가 없는 경우
        User findUser = getUserByIdFromRepository(id);

        // 캐시에 사용자 정보 저장
        redisService.putValueInHash(USER_CACHE_KEY, fieldKey, findUser);

        return findUser;
    }

    @Transactional(readOnly = true)
    public User getUserByName(String name) {
        String fieldKey = name;

        // 캐시 히트: 캐시에 이름 -> ID 매핑이 있는 경우
        if (redisService.hasFieldInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey)) {
            Long userId = redisService.getValueFromHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey, Long.class);
            return getUserById(userId);
        }

        // 캐시 미스: 캐시에 이름 -> ID 매핑이 없는 경우
        User user = getUserByNameFromRepository(name);

        // 이름과 ID의 매핑을 캐시에 저장
        redisService.putValueInHash(USER_NAME_TO_ID_MAPPING_CACHE_KEY, fieldKey, user.getId());

        // ID로 사용자 정보 저장 (findById를 통해 캐싱도 처리됨)
        return getUserById(user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByWorkEmail(String workEmail) {
        // 로그인과 관련된 정보는 직접 DB에서 조회
        return userRepository.findByWorkEmail(workEmail)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EMAIL));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByWorkEmailAndPassword(String workEmail,
                                              String password) {
        // 로그인과 관련된 정보는 직접 DB에서 조회
        return userRepository.findByWorkEmailAndPassword(workEmail, password)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_EMAIL));
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findBySearchQuery(String searchQuery) {
        // 검색과 관련된 정보는 직접 DB에서 조회
        return userRepository.findByNameContainingIgnoreCase(searchQuery);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResponseUserInfoDto> getUserInfosByUserIds(List<Long> userIds) {
        return userIds.stream()
                .map(this::getUserById)
                .map(UserDtoConverter::toResponseUserInfoDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseUserChatDto getUserChatById(Long userId) {
        User user = getUserById(userId);
        return toResponseUserChatDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseOnlineStatusDto getOnlineStatus(Long userId) {
        User user = getUserById(userId);
        return toResponseOnlineStatusDto(userId, user.getOnlineStatus());
    }

    @Transactional
    @Override
    public User save(RequestRegisterDto userDto) {
        try {
            User registerUser = createUser(userDto);
            User savedUser = userRepository.save(registerUser);

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

    @Transactional
    @Override
    public User updateOnlineStatus(UpdateOnlineStatusDto dto) {
        User foundUser = getUserById(dto.getUserId());

        foundUser.updateOnlineStatus(dto.getOnlineStatus());

        userRepository.save(foundUser);

        // 변경된 사용자 정보를 캐시에 업데이트
        redisService.putValueInHash(USER_CACHE_KEY, foundUser.getId().toString(), foundUser);

        // Redis 채널을 통해 다른 서비스에 상태 변경 전파
        redisService.publishMessage("user.online.status", dto);

        return foundUser;
    }

    @Transactional(readOnly = true)
    @Override
    public void handleOnlineStatusUpdate(UpdateOnlineStatusDto dto) {
        Long updatedUserId = dto.getUserId();
        String onlineStatus = dto.getOnlineStatus();

        List<Long> followerIds = followCrudService.findFollowerIds(updatedUserId);
        List<Long> followingIds = followCrudService.findFollowingIds(updatedUserId);

        Stream.concat(followerIds.stream(), followingIds.stream())
                .distinct()
                .forEach(chatUserId -> {
                    String destination = "/topic/users/status/" + chatUserId;

                    ResponseOnlineStatusDto response =
                            toResponseOnlineStatusDto(updatedUserId, onlineStatus);
                    messagingTemplate.convertAndSend(destination, response);
                });
    }

    @Transactional
    @Override
    public User updatePassword(UpdatePasswordDto dto) {
        // 로그인과 관련된 정보는 직접 DB에서 조회
        User user = getUserByIdFromRepository(dto.getUserId());

        user.updatePassword(dto.getPassword());

        return user;
    }

    private User getUserByIdFromRepository(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID + "id=" + id));
    }

    private User getUserByNameFromRepository(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_NAME + "name=" + name));
    }
}
