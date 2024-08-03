package com.pigeon_stargram.sns_clone.controller;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.dto.AddCommentDto;
import com.pigeon_stargram.sns_clone.dto.CommentDto;
import com.pigeon_stargram.sns_clone.dto.DataDto;
import com.pigeon_stargram.sns_clone.dto.PostsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/comments")
@RestController
public class CommentsController {

    @Autowired
    private TestData testData;

    @PostMapping("/add")
    public List<PostsDto> addComment(@RequestBody AddCommentDto comment) {
        log.info("addComment: {}", comment);
        List<CommentDto> comments = testData.postsDtoList.stream()
                .filter(postsDto -> postsDto.getId().equals(comment.getPostId()))
                .findFirst()
                .map(PostsDto::getData)
                .map(DataDto::getComments)
                .get();
        comments.addFirst(comment.getComment());
        return testData.postsDtoList;
    }
}
