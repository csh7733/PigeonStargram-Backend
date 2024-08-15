package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;

import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionConst.*;

interface UserService {

    User findById(Long id);

    User findByWorkEmail(String email);

    User findByWorkEmailAndPassword(String email, String password);

    List<User> findAll();

    ResponseUserChatDto findUserChatById(Long userId);

    User save(User user);

    User save(RequestRegisterDto userDto);

    List<User> saveAll(List<UserDto> userDtoList);

    List<User> saveAllUser(List<User> users);

    void updateOnlineStatus(User user, String onlineStatus);

    void updatePassword(User user, String newPassword);

    ResponseOnlineStatusDto getOnlineStatus(Long id);

}
