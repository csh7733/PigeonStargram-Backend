package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.LoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ChatService chatService;

    public User findByWorkEmailAndPassword(String email,String password){
        return userRepository.findByWorkEmailAndPassword(email,password)
                .orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User save(RegisterDto userDto) {
        return userRepository.save(userDto.toEntity());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserChatDto findUserForChat(Long userId) {
        return new UserChatDto(findById(userId));
    }

    public List<User> saveAll(List<UserDto> userDtoList) {
        List<User> users = userDtoList.stream()
                .map(UserDto::toEntity)
                .collect(Collectors.toList());
        return userRepository.saveAll(users);
    }

    public void updateOnlineStatus(User user,String onlineStatus){
        user.updateOnlineStatus(onlineStatus);
    }

    public String getOnlineStatus(User user){
        return user.getOnlineStatus();
    }

    public User findByEmail(String email) {
        return userRepository.findByWorkEmail(email).orElse(null);
    }
    public List<User> saveAllUser(List<User> users) {
        return userRepository.saveAll(users);
    }

    public void updatePassword(User user,String newPassword){
        user.updatePassword(newPassword);
    }
}
