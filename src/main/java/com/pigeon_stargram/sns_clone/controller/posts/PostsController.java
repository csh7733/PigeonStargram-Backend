package com.pigeon_stargram.sns_clone.controller.posts;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post2.*;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {

    @Autowired
    private TestData testData;
    private final PostsService postsService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public List<PostsDto> getPosts() {
//        log.info("getPosts: {}", testData.postsDtoList);
//        return testData.postsDtoList;
        User user = userRepository.findById(1L).get();
        return postsService.getPostsByUser(user);
    }

    @PostMapping("/editComment")
    public List<PostsDto2> editComment(@RequestBody EditCommentDto2 dto) {
        log.info("editComment: {}", dto);
        testData.postsDtoList.forEach(post -> {
            if (post.getId().equals(dto.getKey())) {
                List<CommentDto2> comments = post.getData().getComments();

                if (comments == null) {
                    comments = new LinkedList<>();
                    post.getData().setComments(comments);
                }

                
                comments.add(0, dto.getId());
            }
        });
        return testData.postsDtoList;
    }

    @PostMapping("/list/like")
    public List<PostsDto2> likePost(@RequestBody LikePostDto2 dto) {
        log.info("likePost: {}", dto);
        LikeDto2 likeDto = testData.findPostById(dto.getPostId())
                .map(PostsDto2::getData)
                .map(DataDto2::getLikes)
                .get();

        likeDto.setLike(!likeDto.isLike());
        likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        return testData.postsDtoList;
    }

}
