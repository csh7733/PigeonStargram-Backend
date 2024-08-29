package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

import java.util.List;

public interface UserService {

    User findById(Long id);

    User findByName(String name);
    List<User> findBySearchQuery(String searchQuery);

    User findByWorkEmail(String email);

    User findByWorkEmailAndPassword(String email, String password);

    ResponseUserChatDto findUserChatById(Long userId);

    User save(RequestRegisterDto userDto);

    List<User> saveAll(List<UserDto> userDtoList);

    User updateOnlineStatus(UpdateOnlineStatusDto updateOnlineStatusDto);
    public void handleOnlineStatusUpdate(UpdateOnlineStatusDto dto);

    User updatePassword(UpdatePasswordDto updatePasswordDto);

    ResponseOnlineStatusDto getOnlineStatus(Long id);

    List<ResponseUserInfoDto> getUserInfosByUserIds(List<Long> userIds);

}
