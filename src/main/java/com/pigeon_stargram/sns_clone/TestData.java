package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.response.PostsDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.Follow.FollowerDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestData {

    private final UserRepository userRepository;
    private final PostsService postsService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final JsonUtil jsonUtil;

    private final UserService userService;
    private final FollowService followService;

    public List<UserDto> userDtoList;

    @PostConstruct
    public void initData1() throws IOException {
        log.info("init data");
        ObjectMapper objectMapper = new ObjectMapper();
        userDtoList = objectMapper.readValue(
                new ClassPathResource("data/chat.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserDto.class));
        userService.saveAll(userDtoList);
        followService.createFollow(new AddFollowDto(1L, 10L));
        followService.createFollow(new AddFollowDto(1L, 11L));
    }

    @PostConstruct
    public void initData2() {
        // Creating Users
        User johnDoe = new User("John Doe", "img-user.png");
        User janeSmith = new User("Jane Smith", "avatar-2.png");
        User aliceBrown = new User("Alice Brown", "avatar-3.png");

        userService.save(johnDoe);
        userService.save(janeSmith);
        userService.save(aliceBrown);

        // Post 1 by John Doe
        Posts post1 = postsService.createPost(johnDoe, "Hello from John Doe!");
        Comment post1_comment1 = commentService.createComment(janeSmith, post1, "Hi John!");
        replyService.createReply(aliceBrown, post1_comment1, "Hello Jane!");

        // Post 2 by Jane Smith
        List<Image> images2 = List.of(new Image("img-profile1.png", true));
        Posts post2 = postsService.createPost(janeSmith, "Jane's beautiful day at the park.", images2);
        Comment post2_comment1 = commentService.createComment(johnDoe, post2, "Looks great, Jane!");
        replyService.createReply(aliceBrown, post2_comment1, "Amazing picture!");
        Comment post2_comment2 = commentService.createComment(aliceBrown, post2, "Love the scenery.");

        // Post 3 by Alice Brown
        List<Image> images3 = List.of(new Image("img-profile2.jpg", true), new Image("img-profile3.jpg", true));
        Posts post3 = postsService.createPost(aliceBrown, "Alice's adventure in the mountains.", images3);
        Comment post3_comment1 = commentService.createComment(johnDoe, post3, "Wow, awesome view!");
        replyService.createReply(janeSmith, post3_comment1, "I agree, it's stunning!");
        Comment post3_comment2 = commentService.createComment(janeSmith, post3, "Wish I was there!");

        // Post 4 by John Doe
        Posts post4 = postsService.createPost(johnDoe, "Back to work after a great vacation.");
        Comment post4_comment1 = commentService.createComment(aliceBrown, post4, "Hope you had a good time!");
        replyService.createReply(janeSmith, post4_comment1, "Welcome back!");

        // Logging Posts
        List<PostsDto> postsByJohnDoe = postsService.getPostsByUser(johnDoe);
        List<PostsDto> postsByJaneSmith = postsService.getPostsByUser(janeSmith);
        List<PostsDto> postsByAliceBrown = postsService.getPostsByUser(aliceBrown);

        logPosts(postsByJohnDoe);
        logPosts(postsByJaneSmith);
        logPosts(postsByAliceBrown);

        log.info("Print finish");
    }

    @PostConstruct
    public void initData3(){
        log.info("init data3");
    }


    private void logPosts(List<PostsDto> posts) {
        posts.forEach(post -> {
            String jsonString = jsonUtil.toJson(post);
            log.info(jsonString);
        });
    }

}
