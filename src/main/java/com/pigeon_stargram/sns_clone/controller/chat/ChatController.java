package com.pigeon_stargram.sns_clone.controller.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.chat.request.ChatPartnerDto;
import com.pigeon_stargram.sns_clone.dto.chat.request.GetChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.request.NewChatDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.UserChatDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @PostMapping("/users/id")
    public UserChatDto getUserById(@RequestBody ChatPartnerDto request) {
        Long userId = request.getId();
        return userService.findUserForChat(userId);
    }

    @GetMapping("/users")
    public List<UserChatDto> getAllUsers() {
        log.info("users!!!");
        return userService.findAllUsersForChat();
    }

    @PostMapping("/filter")
    public List<ChatHistoryDto> getUserChats(@RequestBody GetChatHistoryDto request) {
        Long user1Id = request.getUser1Id();
        Long user2Id = request.getUser2Id();

        return chatService.getUserChats(user1Id,user2Id);
    }

    @PostMapping("/insert")
    public void insertChat(@RequestBody NewChatDto request) {
        chatService.save(request);
    }

//    @PostMapping("/users/modify")
//    public List<UserDto> modifyUser(@RequestBody UserDto userDto) {
//        if (userDto.getId() != 0) {
//            users.stream().filter(u -> u.getId() == userDto.getId()).findFirst().ifPresent(u -> {
//                u.setName(userDto.getName());
//                u.setCompany(userDto.getCompany());
//                u.setRole(userDto.getRole());
//                u.setWorkEmail(userDto.getWorkEmail());
//                u.setPersonalEmail(userDto.getPersonalEmail());
//                u.setWorkPhone(userDto.getWorkPhone());
//                u.setPersonalPhone(userDto.getPersonalPhone());
//                u.setLocation(userDto.getLocation());
//                u.setAvatar(userDto.getAvatar());
//                u.setStatus(userDto.getStatus());
//                u.setLastMessage(userDto.getLastMessage());
//                u.setBirthdayText(userDto.getBirthdayText());
//                u.setUnReadChatCount(userDto.getUnReadChatCount());
//                u.setOnlineStatus(userDto.getOnlineStatus());
//            });
//        } else {
//            userDto.setId(users.size() + 1);
//            users.add(userDto);
//        }
//        return users;
//    }
}
