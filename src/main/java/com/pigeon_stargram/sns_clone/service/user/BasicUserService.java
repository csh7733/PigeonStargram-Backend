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
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.follow.FollowBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;
import static com.pigeon_stargram.sns_clone.service.chat.ChatBuilder.buildResponseOnlineStatusDto;
import static com.pigeon_stargram.sns_clone.service.user.UserBuilder.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BasicUserService implements UserService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID + "id=" + id));
    }

    public User findByName(String name){
        return userRepository.findByName(name)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_NAME));
    }

    @Override
    public List<User> findBySearchQuery(String searchQuery) {
        return userRepository.findByNameContainingIgnoreCase(searchQuery);
    }

    public User findByWorkEmail(String email) {
        List<User> findUsers = userRepository.findByWorkEmail(email);
        if (findUsers.isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND_EMAIL);
        } else if (findUsers.size() > 1) {
            throw new MultipleUsersFoundException(MULTIPLE_USERS_FOUND_EMAIL);
        }
        return findUsers.getFirst();
    }

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
        User user = findById(userId);
        return buildResponseUserChatDto(user);
    }

    public User save(RequestRegisterDto userDto) {
        try {
            return userRepository.save(userDto.toEntity());
        } catch (DataIntegrityViolationException e) {
            throw new RegisterFailException(REGISTER_FAIL_EMAIL, e);
        }
    }

    // TestData에서만 사용
    public List<User> saveAll(List<UserDto> userDtoList) {
        List<User> users = userDtoList.stream()
                .map(UserDto::toEntity)
                .toList();
        return userRepository.saveAll(users);
    }

    @Override
    public User updateOnlineStatus(UpdateOnlineStatusDto dto) {
        User user = findById(dto.getUserId());
        user.updateOnlineStatus(dto.getOnlineStatus());

        sentOnlineStatus(dto);

        return user;
    }

    public void sentOnlineStatus(UpdateOnlineStatusDto dto) {
        Long userId = dto.getUserId();
        String onlineStatus = dto.getOnlineStatus();

        User user = findById(userId);
        List<ResponseFollowerDto> followerDtos = getFollowerDtos(user);
        List<ResponseFollowerDto> followingDtos = getFollowingDtos(user);

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

    private static List<ResponseFollowerDto> getFollowingDtos(User user) {
        return user.getFollowings().stream()
                .map(Follow::getRecipient)
                .map(FollowBuilder::buildResponseFollowerDto)
                .toList();
    }

    private static List<ResponseFollowerDto> getFollowerDtos(User user) {
        return user.getFollowers().stream()
                .map(Follow::getSender)
                .map(FollowBuilder::buildResponseFollowerDto)
                .toList();
    }

    @Override
    public User updatePassword(UpdatePasswordDto dto) {
        User user = findById(dto.getUserId());
        user.updatePassword(dto.getPassword());
        return user;
    }

    public ResponseOnlineStatusDto getOnlineStatus(Long id) {
        User user = findById(id);
        return buildResponseOnlineStatusDto(user.getId(), user.getOnlineStatus());
    }
}
