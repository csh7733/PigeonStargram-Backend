package com.pigeon_stargram.sns_clone.controller.posts;

import com.pigeon_stargram.sns_clone.TestData;
import com.pigeon_stargram.sns_clone.dto.posts.*;
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
    public List<PostsDto> addReply(@RequestBody AddReplyDto dto) {
        log.info("addReply: {}", dto);
        List<ReplyDto> replyDtos = testData.findCommentById(dto.getPostId(), dto.getCommentId())
                .map(CommentDto::getData)
                .map(CommentDataDto::getReplies)
                .get();
        replyDtos.addFirst(dto.getReply());
        return testData.postsDtoList;
    }

    @PostMapping("/list/like")
    public List<PostsDto> likeReply(@RequestBody LikeReplyDto dto) {
        log.info("likeComment: {}", dto);
        LikeDto likeDto = testData.findReplyById(dto.getPostId(), dto.getCommentId(), dto.getReplayId())
                .map(ReplyDto::getData)
                .map(ReplyDataDto::getLikes)
                .get();

        likeDto.setLike(!likeDto.isLike());
        likeDto.setValue(likeDto.isLike() ? likeDto.getValue() + 1 : likeDto.getValue() - 1);
        return testData.postsDtoList;
    }
}
