package com.pigeon_stargram.sns_clone.controller.posts;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.dto.posts.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequestMapping("/api/posts")
@RestController
public class PostsController {

    @Autowired
    private TestData testData;

    @GetMapping("/list")
    public List<PostsDto> getPosts() {
        log.info("getPosts: {}", testData.postsDtoList);
        return testData.postsDtoList;
    }

    @PostMapping("/editComment")
    public List<PostsDto> editComment(@RequestBody EditCommentDto dto) {
        log.info("editComment: {}", dto);
        testData.postsDtoList.forEach(post -> {
            if (post.getId().equals(dto.getKey())) {
                List<CommentDto> comments = post.getData().getComments();

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
    public List<PostsDto> likePost(@RequestBody LikePostDto dto) {
        log.info("likePost: {}", dto);
        LikeDto likeDto = testData.findPostById(dto.getPostId())
                .map(PostsDto::getData)
                .map(DataDto::getLikes)
                .get();

        likeDto.setLike(!likeDto.isLike());
        likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        return testData.postsDtoList;
    }

}
