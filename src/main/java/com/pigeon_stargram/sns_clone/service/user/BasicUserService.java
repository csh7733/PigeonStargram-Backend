package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID));
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
        return new ResponseUserChatDto(findById(userId));
    }

    // 일단 안씀
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 일단 안씀
    public User save(User user) {
        return userRepository.save(user);
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

    // 일단 안씀
    public List<User> saveAllUser(List<User> users) {
        return userRepository.saveAll(users);
    }

    public User updateOnlineStatus(Long userId, String onlineStatus) {
        User user = findById(userId);
        user.updateOnlineStatus(onlineStatus);
        return user;
    }

    public User updatePassword(Long userId, String newPassword) {
        User user = findById(userId);
        user.updatePassword(newPassword);
        return user;
    }

    public ResponseOnlineStatusDto getOnlineStatus(Long id) {
        User user = findById(id);
        return new ResponseOnlineStatusDto(user);
    }
}
