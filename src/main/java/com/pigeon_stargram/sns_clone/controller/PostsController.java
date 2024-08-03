package com.pigeon_stargram.sns_clone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.dto.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
        log.info("editComment: {}", testData.postsDtoList);

        return testData.postsDtoList;
    }

    @PostMapping("/list/like")
    public List<PostsDto> likePost(@RequestBody LikePostDto dto){
        log.info("likePost: {}", dto);
        Optional<PostsDto> post = testData.postsDtoList.stream()
                .filter(postsDto -> postsDto.getId().equals(dto.getPostId()))
                .findFirst();

        Optional<LikeDto> likes = post.map(PostsDto::getData).map(DataDto::getLikes);
        likes.ifPresent(likeDto -> {
            likeDto.setLike(!likeDto.isLike());
            likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        });
        return testData.postsDtoList;
    }
}
