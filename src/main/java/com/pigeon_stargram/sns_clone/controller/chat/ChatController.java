package com.pigeon_stargram.sns_clone.controller.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.chat.ChatHistoryDto;
import com.pigeon_stargram.sns_clone.dto.chat.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {

    private List<UserDto> users = new ArrayList<>();
    private List<ChatHistoryDto> chatHistories = new ArrayList<>();

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(new ClassPathResource("data/chat.json").getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));
        chatHistories = objectMapper.readValue(new ClassPathResource("data/chat-history.json").getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, ChatHistoryDto.class));
    }

    @PostMapping("/users/id")
    public UserDto getUserById(@RequestBody UserDto userDto) {
        log.info("id = {}",userDto.getId());
        log.info("userDto = {}",userDto.toString());
        return users.stream().filter(u -> u.getId() == userDto.getId()).findFirst().orElse(null);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        log.info("users = {}",users);
        return users;
    }

    @PostMapping("/users/modify")
    public List<UserDto> modifyUser(@RequestBody UserDto userDto) {
        if (userDto.getId() != 0) {
            users.stream().filter(u -> u.getId() == userDto.getId()).findFirst().ifPresent(u -> {
                u.setName(userDto.getName());
                u.setCompany(userDto.getCompany());
                u.setRole(userDto.getRole());
                u.setWorkEmail(userDto.getWorkEmail());
                u.setPersonalEmail(userDto.getPersonalEmail());
                u.setWorkPhone(userDto.getWorkPhone());
                u.setPersonalPhone(userDto.getPersonalPhone());
                u.setLocation(userDto.getLocation());
                u.setAvatar(userDto.getAvatar());
                u.setStatus(userDto.getStatus());
                u.setLastMessage(userDto.getLastMessage());
                u.setBirthdayText(userDto.getBirthdayText());
                u.setUnReadChatCount(userDto.getUnReadChatCount());
                u.setOnlineStatus(userDto.getOnlineStatus());
            });
        } else {
            userDto.setId(users.size() + 1);
            users.add(userDto);
        }
        return users;
    }

    @PostMapping("/filter")
    public List<ChatHistoryDto> getUserChats(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("user");
        log.info("filter userName = {}", userId);
        return chatHistories.stream().filter(chat -> chat.getFrom().equals(userId) || chat.getTo().equals(userId)).collect(Collectors.toList());
    }


    @PostMapping("/insert")
    public List<ChatHistoryDto> insertChat(@RequestBody ChatHistoryDto chatDto) {
        log.info("chatDto = {}",chatDto.toString());
        chatDto.setId(chatHistories.size() + 1);
        chatHistories.add(chatDto);
        return chatHistories;
    }

    @PostMapping("/insert2")
    public ChatHistoryDto insertChat2(@RequestBody ChatHistoryDto chatDto) {
        return chatDto;
    }
}
