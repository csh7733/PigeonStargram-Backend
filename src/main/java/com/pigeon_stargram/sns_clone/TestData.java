package com.pigeon_stargram.sns_clone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.posts.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TestData {

    public List<PostsDto> postsDtoList;

    @PostConstruct
    public void init() throws IOException {
        log.info("PostsController init");
        ObjectMapper objectMapper = new ObjectMapper();
        postsDtoList = objectMapper.readValue(
                new ClassPathResource("data/posts.json").getFile(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, PostsDto.class));
    }

    public Optional<PostsDto> findPostById(String postId) {
        return postsDtoList.stream()
                .filter(postsDto -> postsDto.getId().equals(postId))
                .findFirst();
    }

    public Optional<CommentDto> findCommentById(String postId, String commentId) {
        return findPostById(postId)
                .map(PostsDto::getData)
                .map(DataDto::getComments)
                .get().stream()
                .filter(commentDto -> commentDto.getId().equals(commentId))
                .findFirst();
    }

    public Optional<ReplyDto> findReplyById(String postId, String commentId, String replyId) {
        return findCommentById(postId, commentId)
                .map(CommentDto::getData)
                .map(CommentDataDto::getReplies)
                .get().stream()
                .filter(replyDto -> replyDto.getId().equals(replyId))
                .findFirst();
    }
}
