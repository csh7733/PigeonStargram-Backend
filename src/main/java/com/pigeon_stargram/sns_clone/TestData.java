package com.pigeon_stargram.sns_clone;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import com.pigeon_stargram.sns_clone.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestData {

    private final UserRepository userRepository;
    private final PostsService postsService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final JsonUtil jsonUtil;

    @PostConstruct
    public void initData() {
        User johnDoe = new User("John Doe", "img-user.png");
        userRepository.save(johnDoe);

        // Post 1
        Posts post1 = postsService.createPost(johnDoe, "켄터22키");

        Comment post1_comment1 = commentService.createComment(johnDoe, post1, "Test");

        // Post 2
        List<Image> images2 = List.of(new Image("img-profile1.png", true));
        Posts post2 = postsService.createPost(johnDoe, "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. There are many variations of passages.", images2);

        Comment post2_comment1 = commentService.createComment(johnDoe, post2, "Test");
        replyService.createReply(johnDoe, post2_comment1, "Test Reply");
        replyService.createReply(johnDoe, post2_comment1, "Demo");

        Comment post2_comment2 = commentService.createComment(johnDoe, post2, "It is a long established fact that a reader will be distracted by the readable content of a page.");
        Comment post2_comment3 = commentService.createComment(johnDoe, post2, "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.There are many variations of passages.");
        replyService.createReply(johnDoe, post2_comment3, "It is a long established fact that a reader will be distracted by the readable content.");

        // Post 3
        List<Image> images3 = List.of(new Image("img-profile2.jpg", true), new Image("img-profile3.jpg", true));
        Posts post3 = postsService.createPost(johnDoe, "It is a long established fact that a reader will be distracted by the readable content of a page", images3);

        Comment post3_comment1 = commentService.createComment(johnDoe, post3, "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.");
        replyService.createReply(johnDoe, post3_comment1, "Test");
        replyService.createReply(johnDoe, post3_comment1, "Test To Reply");
        replyService.createReply(johnDoe, post3_comment1, "test");

        // Post 4
//        Posts post4 = postsService.createPost(johnDoe, "It is a long established fact that a reader will be distracted by the readable content of a page");
        // Assuming there's a way to set video (which isn't shown in the Posts entity above)
        // post4.setVideo("vyJU9efvUtQ");

        List<PostsDto> postsByUser = postsService.getPostsByUser(johnDoe);
        postsByUser.forEach(post -> {
            String jsonString = jsonUtil.toJson(post);
            log.info(jsonString);
        });
        log.info("Print finish");
    }
}
