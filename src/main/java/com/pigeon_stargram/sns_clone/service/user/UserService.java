package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;

import java.util.List;

public interface UserService {

    User findById(Long id);
    User findByName(String name);

    User findByWorkEmail(String email);

    User findByWorkEmailAndPassword(String email, String password);

    List<User> findAll();

    ResponseUserChatDto findUserChatById(Long userId);

    User save(RequestRegisterDto userDto);

    List<User> saveAll(List<UserDto> userDtoList);

    List<User> saveAllUser(List<User> users);

    User updateOnlineStatus(Long userId, String onlineStatus);

    User updatePassword(Long userId, String newPassword);

    ResponseOnlineStatusDto getOnlineStatus(Long id);

}
