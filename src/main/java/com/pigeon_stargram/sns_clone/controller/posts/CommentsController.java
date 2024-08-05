package com.pigeon_stargram.sns_clone.controller.posts;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.dto.post2.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/comments")
@RestController
public class CommentsController {

    @Autowired
    private TestData testData;

    @PostMapping("/add")
    public List<PostsDto2> addComment(@RequestBody AddCommentDto2 comment) {
        log.info("addComment: {}", comment);
        List<CommentDto2> comments = testData.findPostById(comment.getPostId())
                .map(PostsDto2::getData)
                .map(DataDto2::getComments)
                .get();
        comments.addFirst(comment.getComment());
        return testData.postsDtoList;
    }

    @PostMapping("/list/like")
    public List<PostsDto2> likeComment(@RequestBody LikeCommentDto2 dto) {
        log.info("likeComment: {}", dto);
        LikeDto2 likeDto = testData.findCommentById(dto.getPostId(), dto.getCommentId())
                .map(CommentDto2::getData)
                .map(CommentDataDto2::getLikes)
                .get();

        likeDto.setLike(!likeDto.isLike());
        likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        return testData.postsDtoList;
    }
}
