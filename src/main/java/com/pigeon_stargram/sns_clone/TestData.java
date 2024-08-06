package com.pigeon_stargram.sns_clone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.dto.posts.*;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestData {

    public List<PostsDto> postsDtoList;
    public List<UserDto> userDtoList;
    public List<FollowerDto> followerDtoList;

    private final UserService userService;
    private final FollowService followService;

//    @PostConstruct
    public void initData() throws IOException {
        log.info("init data");
        ObjectMapper objectMapper = new ObjectMapper();
        userDtoList = objectMapper.readValue(
                new ClassPathResource("data/chat.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserDto.class));
        userService.saveAll(userDtoList);
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("PostsController init");
        ObjectMapper objectMapper = new ObjectMapper();
        postsDtoList = objectMapper.readValue(
                new ClassPathResource("data/posts.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, PostsDto.class));
    }

    public Optional<PostsDto> findPostById(String postId) {
        return postsDtoList.stream()
                .filter(postsDto -> postsDto.getId().equals(postId))
                .findFirst();
    }

    public Optional<CommentDto> findCommentById(String postId, String commentId) {
        return findPostById(postId)
                .map(PostsDto::getData)
                .map(DataDto::getComments)
                .get().stream()
                .filter(commentDto -> commentDto.getId().equals(commentId))
                .findFirst();
    }

    public Optional<ReplyDto> findReplyById(String postId, String commentId, String replyId) {
        return findCommentById(postId, commentId)
                .map(CommentDto::getData)
                .map(CommentDataDto::getReplies)
                .get().stream()
                .filter(replyDto -> replyDto.getId().equals(replyId))
                .findFirst();
    }

}
