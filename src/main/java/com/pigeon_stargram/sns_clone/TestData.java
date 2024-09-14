package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.Follow.internal.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
        followService.createFollow(new AddFollowDto(1L, 2L));
        followService.createFollow(new AddFollowDto(1L, 3L));
        followService.createFollow(new AddFollowDto(1L, 4L));
        followService.createFollow(new AddFollowDto(1L, 5L));
        followService.createFollow(new AddFollowDto(2L, 6L));
        followService.createFollow(new AddFollowDto(2L, 7L));
        followService.createFollow(new AddFollowDto(2L, 8L));
        followService.createFollow(new AddFollowDto(2L, 9L));
        followService.createFollow(new AddFollowDto(3L, 10L));
        followService.createFollow(new AddFollowDto(3L, 11L));
        followService.createFollow(new AddFollowDto(3L, 12L));
        followService.createFollow(new AddFollowDto(3L, 13L));
        followService.createFollow(new AddFollowDto(4L, 14L));
        followService.createFollow(new AddFollowDto(4L, 15L));
        followService.createFollow(new AddFollowDto(5L, 1L));
        followService.createFollow(new AddFollowDto(5L, 6L));
        followService.createFollow(new AddFollowDto(6L, 2L));
        followService.createFollow(new AddFollowDto(6L, 3L));
        followService.createFollow(new AddFollowDto(7L, 4L));
        followService.createFollow(new AddFollowDto(7L, 5L));
        followService.createFollow(new AddFollowDto(8L, 7L));
        followService.createFollow(new AddFollowDto(8L, 8L));
        followService.createFollow(new AddFollowDto(9L, 9L));
        followService.createFollow(new AddFollowDto(10L, 10L));
        followService.createFollow(new AddFollowDto(12L, 12L));
        followService.createFollow(new AddFollowDto(13L, 13L));
        followService.createFollow(new AddFollowDto(14L, 14L));
        followService.createFollow(new AddFollowDto(15L, 15L));
        followService.createFollow(new AddFollowDto(1L, 6L));
        followService.createFollow(new AddFollowDto(2L, 10L));
        followService.createFollow(new AddFollowDto(3L, 7L));
        followService.createFollow(new AddFollowDto(4L, 8L));
        followService.createFollow(new AddFollowDto(5L, 9L));
        followService.createFollow(new AddFollowDto(6L, 11L));
        followService.createFollow(new AddFollowDto(7L, 12L));
        followService.createFollow(new AddFollowDto(8L, 13L));
        followService.createFollow(new AddFollowDto(9L, 14L));
        followService.createFollow(new AddFollowDto(10L, 15L));
        followService.createFollow(new AddFollowDto(11L, 1L));
        followService.createFollow(new AddFollowDto(12L, 2L));
        followService.createFollow(new AddFollowDto(13L, 3L));
        followService.createFollow(new AddFollowDto(14L, 4L));
        followService.createFollow(new AddFollowDto(15L, 5L));
        followService.createFollow(new AddFollowDto(1L, 11L));
        followService.createFollow(new AddFollowDto(2L, 12L));
        followService.createFollow(new AddFollowDto(5L, 15L));
        followService.createFollow(new AddFollowDto(6L, 1L));
        followService.createFollow(new AddFollowDto(7L, 2L));
        followService.createFollow(new AddFollowDto(8L, 3L));
        followService.createFollow(new AddFollowDto(9L, 4L));
        followService.createFollow(new AddFollowDto(10L, 5L));
        followService.createFollow(new AddFollowDto(11L, 6L));
        followService.createFollow(new AddFollowDto(11L, 3L));
        followService.createFollow(new AddFollowDto(12L, 7L));
        followService.createFollow(new AddFollowDto(13L, 8L));
        followService.createFollow(new AddFollowDto(14L, 9L));
        followService.createFollow(new AddFollowDto(15L, 10L));
        followService.createFollow(new AddFollowDto(1L, 7L));
        followService.createFollow(new AddFollowDto(2L, 14L));
        followService.createFollow(new AddFollowDto(3L, 15L));
        followService.createFollow(new AddFollowDto(4L, 11L));
        followService.createFollow(new AddFollowDto(5L, 12L));
        followService.createFollow(new AddFollowDto(6L, 13L));
    }



    private void logPosts(List<ResponsePostDto> posts) {
        posts.forEach(post -> {
            String jsonString = jsonUtil.toJson(post);
            log.info(jsonString);
        });
    }

}
