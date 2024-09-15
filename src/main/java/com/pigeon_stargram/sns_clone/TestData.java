package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.comment.CommentServiceV2;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.pigeon_stargram.sns_clone.dto.Follow.FollowDtoConverter.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestData {

    private final PostService postService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final JsonUtil jsonUtil;

    private final BasicUserService userService;
    private final FollowService followService;
    private final ChatService chatService;

    private final RedisTemplate<String, Object> redisTemplate;

    public List<UserDto> userDtoList;

    @PostConstruct
    public void initData() throws IOException {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        initData1();
        initData2();
    }

    public void initData1() throws IOException {
        log.info("init data");
        ObjectMapper objectMapper = new ObjectMapper();
        userDtoList = objectMapper.readValue(
                new ClassPathResource("data/chat.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserDto.class));
        userService.saveAll(userDtoList);

    }

    public void initData2() {
        followService.createFollow(toAddFollowDto(1L, 2L));
        followService.createFollow(toAddFollowDto(1L, 3L));
        followService.createFollow(toAddFollowDto(1L, 4L));
        followService.createFollow(toAddFollowDto(1L, 5L));
        followService.createFollow(toAddFollowDto(2L, 6L));
        followService.createFollow(toAddFollowDto(2L, 7L));
        followService.createFollow(toAddFollowDto(2L, 8L));
        followService.createFollow(toAddFollowDto(2L, 9L));
        followService.createFollow(toAddFollowDto(3L, 10L));
        followService.createFollow(toAddFollowDto(3L, 11L));
        followService.createFollow(toAddFollowDto(3L, 12L));
        followService.createFollow(toAddFollowDto(3L, 13L));
        followService.createFollow(toAddFollowDto(4L, 14L));
        followService.createFollow(toAddFollowDto(4L, 15L));
        followService.createFollow(toAddFollowDto(5L, 1L));
        followService.createFollow(toAddFollowDto(5L, 6L));
        followService.createFollow(toAddFollowDto(6L, 2L));
        followService.createFollow(toAddFollowDto(6L, 3L));
        followService.createFollow(toAddFollowDto(7L, 4L));
        followService.createFollow(toAddFollowDto(7L, 5L));
        followService.createFollow(toAddFollowDto(8L, 7L));
        followService.createFollow(toAddFollowDto(8L, 8L));
        followService.createFollow(toAddFollowDto(9L, 9L));
        followService.createFollow(toAddFollowDto(10L, 10L));
        followService.createFollow(toAddFollowDto(12L, 12L));
        followService.createFollow(toAddFollowDto(13L, 13L));
        followService.createFollow(toAddFollowDto(14L, 14L));
        followService.createFollow(toAddFollowDto(15L, 15L));
        followService.createFollow(toAddFollowDto(1L, 6L));
        followService.createFollow(toAddFollowDto(2L, 10L));
        followService.createFollow(toAddFollowDto(3L, 7L));
        followService.createFollow(toAddFollowDto(4L, 8L));
        followService.createFollow(toAddFollowDto(5L, 9L));
        followService.createFollow(toAddFollowDto(6L, 11L));
        followService.createFollow(toAddFollowDto(7L, 12L));
        followService.createFollow(toAddFollowDto(8L, 13L));
        followService.createFollow(toAddFollowDto(9L, 14L));
        followService.createFollow(toAddFollowDto(10L, 15L));
        followService.createFollow(toAddFollowDto(11L, 1L));
        followService.createFollow(toAddFollowDto(12L, 2L));
        followService.createFollow(toAddFollowDto(13L, 3L));
        followService.createFollow(toAddFollowDto(14L, 4L));
        followService.createFollow(toAddFollowDto(15L, 5L));
        followService.createFollow(toAddFollowDto(1L, 11L));
        followService.createFollow(toAddFollowDto(2L, 12L));
        followService.createFollow(toAddFollowDto(5L, 15L));
        followService.createFollow(toAddFollowDto(6L, 1L));
        followService.createFollow(toAddFollowDto(7L, 2L));
        followService.createFollow(toAddFollowDto(8L, 3L));
        followService.createFollow(toAddFollowDto(9L, 4L));
        followService.createFollow(toAddFollowDto(10L, 5L));
        followService.createFollow(toAddFollowDto(11L, 6L));
        followService.createFollow(toAddFollowDto(11L, 3L));
        followService.createFollow(toAddFollowDto(12L, 7L));
        followService.createFollow(toAddFollowDto(13L, 8L));
        followService.createFollow(toAddFollowDto(14L, 9L));
        followService.createFollow(toAddFollowDto(15L, 10L));
        followService.createFollow(toAddFollowDto(1L, 7L));
        followService.createFollow(toAddFollowDto(2L, 14L));
        followService.createFollow(toAddFollowDto(3L, 15L));
        followService.createFollow(toAddFollowDto(4L, 11L));
        followService.createFollow(toAddFollowDto(5L, 12L));
        followService.createFollow(toAddFollowDto(6L, 13L));

    }



    private void logPosts(List<ResponsePostDto> posts) {
        posts.forEach(post -> {
            String jsonString = jsonUtil.toJson(post);
            log.info(jsonString);
        });
    }

}
