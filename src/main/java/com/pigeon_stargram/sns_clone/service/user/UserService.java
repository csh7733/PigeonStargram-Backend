package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.LastMessageDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.LoginDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
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
    private final ChatService chatService;

    public User login(LoginDto dto){
        String email = dto.getEmail();
        String password = dto.getPassword();
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

    public List<UserChatDto> findAllUsersForChat(Long currentUserId) {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserChatDto userChatDto = new UserChatDto(user);
                    Integer unReadChatCount = chatService.getUnreadChatCount(currentUserId, user.getId());
                    LastMessageDto lastMessage = chatService.getLastMessage(currentUserId, user.getId());
                    userChatDto.setUnReadChatCount(unReadChatCount);
                    userChatDto.setLastMessage(lastMessage.getTime());
                    userChatDto.setStatus(lastMessage.getLastMessage());
                    return userChatDto;
                })
                .collect(Collectors.toList());
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

    public User findByEmail(String email) {
        return userRepository.findByWorkEmail(email).orElse(null);
    }
    public List<User> saveAllUser(List<User> users) {
        return userRepository.saveAll(users);
    }
}
