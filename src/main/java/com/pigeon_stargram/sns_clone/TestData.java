package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.comment.internal.CreateCommentDto;
import com.pigeon_stargram.sns_clone.dto.post.internal.CreatePostDto;
import com.pigeon_stargram.sns_clone.dto.post.response.ResponsePostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.service.chat.ChatService;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.Follow.AddFollowDto;
import com.pigeon_stargram.sns_clone.dto.user.UserDto;
import com.pigeon_stargram.sns_clone.service.follow.FollowService;
import com.pigeon_stargram.sns_clone.service.user.BasicUserService;
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

    private final PostsService postsService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final JsonUtil jsonUtil;

    private final BasicUserService userService;
    private final FollowService followService;
    private final ChatService chatService;

    public List<UserDto> userDtoList;

    @PostConstruct
    public void initData() throws IOException {
        initData1();
        initData2();
        initData3();
    }

    public void initData1() throws IOException {
        log.info("init data");
        ObjectMapper objectMapper = new ObjectMapper();
        userDtoList = objectMapper.readValue(
                new ClassPathResource("data/chat.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, UserDto.class));
        userService.saveAll(userDtoList);
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

    public void initData2() {
        // Finding Users by ID
        User user1 = userService.findById(1L);
        User user2 = userService.findById(2L);
        User user3 = userService.findById(3L);

        // Post 1 by User 1 (John Doe)
        Posts post1 = postsService.createPost(new CreatePostDto(user1, "Hello from John Doe!"));
        Comment post1_comment1 = commentService.createComment(new CreateCommentDto(user2, post1, "Hi John!"));
        replyService.createReply(new CreateReplyDto(user3, post1_comment1, "Hello Jane!"));

        // Post 2_1 by User 2 (Jane Smith)
        Posts post2_1 = postsService.createPost(new CreatePostDto(user2, "Jane Smith"));

        // Post 2 by User 2 (Jane Smith)
        List<Image> images2 = List.of(new Image("img-profile1.png", true));
        Posts post2 = postsService.createPost(user2, "Jane's beautiful day at the park.", images2);
        Comment post2_comment1 = commentService.createComment(new CreateCommentDto(user1, post2, "Looks great, Jane!"));
        replyService.createReply(new CreateReplyDto(user3, post2_comment1, "Amazing picture!"));
        Comment post2_comment2 = commentService.createComment(new CreateCommentDto(user3, post2, "Love the scenery."));

        // Post 3 by User 3 (Alice Brown)
        List<Image> images3 = List.of(new Image("img-profile2.jpg", true), new Image("img-profile3.jpg", true));
        Posts post3 = postsService.createPost(user3, "Alice's adventure in the mountains.", images3);
        Comment post3_comment1 = commentService.createComment(new CreateCommentDto(user1, post3, "Wow, awesome view!"));
        replyService.createReply(new CreateReplyDto(user2, post3_comment1, "I agree, it's stunning!"));
        Comment post3_comment2 = commentService.createComment(new CreateCommentDto(user2, post3, "Wish I was there!"));

        // Post 4 by User 1 (John Doe)
        Posts post4 = postsService.createPost(new CreatePostDto(user1, "Back to work after a great vacation."));
        Comment post4_comment1 = commentService.createComment(new CreateCommentDto(user3, post4, "Hope you had a good time!"));
        replyService.createReply(new CreateReplyDto(user2, post4_comment1, "Welcome back!"));

        // Logging Posts
        List<ResponsePostsDto> postsByUser1 = postsService.getPostsByUser(user1);
        List<ResponsePostsDto> postsByUser2 = postsService.getPostsByUser(user2);
        List<ResponsePostsDto> postsByUser3 = postsService.getPostsByUser(user3);

        logPosts(postsByUser1);
        logPosts(postsByUser2);
        logPosts(postsByUser3);

        log.info("Print finish");
    }

    public void initData3(){
        log.info("init data3");

        chatService.saveChat(15L, 1L, "Sample Text 1");
        chatService.saveChat(15L, 1L, "Sample Text 2");
        chatService.saveChat(1L, 15L, "Sample Text 3");
        chatService.saveChat(1L, 15L, "Sample Text 4");
        chatService.saveChat(15L, 2L, "Sample Text 5");
        chatService.saveChat(2L, 15L, "Sample Text 6");
        chatService.saveChat(15L, 2L, "Sample Text 7");
        chatService.saveChat(2L, 15L, "Sample Text 8");
        chatService.saveChat(15L, 3L, "Sample Text 9");
        chatService.saveChat(3L, 15L, "Sample Text 10");
        chatService.saveChat(15L, 3L, "Sample Text 11");
        chatService.saveChat(3L, 15L, "Sample Text 12");
        chatService.saveChat(15L, 4L, "Sample Text 13");
        chatService.saveChat(4L, 15L, "Sample Text 14");
        chatService.saveChat(15L, 4L, "Sample Text 15");
        chatService.saveChat(4L, 15L, "Sample Text 16");
        chatService.saveChat(15L, 5L, "Sample Text 17");
        chatService.saveChat(5L, 15L, "Sample Text 18");
        chatService.saveChat(15L, 5L, "Sample Text 19");
        chatService.saveChat(5L, 15L, "Sample Text 20");
        chatService.saveChat(15L, 6L, "Sample Text 21");
        chatService.saveChat(6L, 15L, "Sample Text 22");
        chatService.saveChat(15L, 6L, "Sample Text 23");
        chatService.saveChat(6L, 15L, "Sample Text 24");
        chatService.saveChat(15L, 7L, "Sample Text 25");
        chatService.saveChat(7L, 15L, "Sample Text 26");
        chatService.saveChat(15L, 7L, "Sample Text 27");
        chatService.saveChat(7L, 15L, "Sample Text 28");
        chatService.saveChat(15L, 8L, "Sample Text 29");
        chatService.saveChat(8L, 15L, "Sample Text 30");
        chatService.saveChat(15L, 8L, "Sample Text 31");
        chatService.saveChat(8L, 15L, "Sample Text 32");
        chatService.saveChat(15L, 9L, "Sample Text 33");
        chatService.saveChat(9L, 15L, "Sample Text 34");
        chatService.saveChat(15L, 9L, "Sample Text 35");
        chatService.saveChat(9L, 15L, "Sample Text 36");
        chatService.saveChat(15L, 10L, "Sample Text 37");
        chatService.saveChat(10L, 15L, "Sample Text 38");
        chatService.saveChat(15L, 10L, "Sample Text 39");
        chatService.saveChat(10L, 15L, "Sample Text 40");
        chatService.saveChat(15L, 11L, "Sample Text 41");
        chatService.saveChat(11L, 15L, "Sample Text 42");
        chatService.saveChat(15L, 11L, "Sample Text 43");
        chatService.saveChat(11L, 15L, "Sample Text 44");
        chatService.saveChat(15L, 12L, "Sample Text 45");
        chatService.saveChat(12L, 15L, "Sample Text 46");
        chatService.saveChat(15L, 12L, "Sample Text 47");
        chatService.saveChat(12L, 15L, "Sample Text 48");
        chatService.saveChat(15L, 13L, "Sample Text 49");
        chatService.saveChat(13L, 15L, "Sample Text 50");
        chatService.saveChat(15L, 13L, "Sample Text 51");
        chatService.saveChat(13L, 15L, "Sample Text 52");
        chatService.saveChat(1L, 2L, "Sample Text 53");
    }


    private void logPosts(List<ResponsePostsDto> posts) {
        posts.forEach(post -> {
            String jsonString = jsonUtil.toJson(post);
            log.info(jsonString);
        });
    }

}
