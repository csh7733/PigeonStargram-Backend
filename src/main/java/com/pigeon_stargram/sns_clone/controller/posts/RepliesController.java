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
@RequestMapping("/api/replies")
@RestController
public class RepliesController {

    @Autowired
    private TestData testData;

    @PostMapping("/add")
    public List<PostsDto2> addReply(@RequestBody AddReplyDto2 dto) {
        log.info("addReply: {}", dto);
        List<ReplyDto2> replyDtos = testData.findCommentById(dto.getPostId(), dto.getCommentId())
                .map(CommentDto2::getData)
                .map(CommentDataDto2::getReplies)
                .get();
        replyDtos.addFirst(dto.getReply());
        return testData.postsDtoList;
    }

    @PostMapping("/list/like")
    public List<PostsDto2> likeReply(@RequestBody LikeReplyDto2 dto) {
        log.info("likeComment: {}", dto);
        LikeDto2 likeDto = testData.findReplyById(dto.getPostId(), dto.getCommentId(), dto.getReplayId())
                .map(ReplyDto2::getData)
                .map(ReplyDataDto2::getLikes)
                .get();

        likeDto.setLike(!likeDto.isLike());
        likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        return testData.postsDtoList;
    }
}
