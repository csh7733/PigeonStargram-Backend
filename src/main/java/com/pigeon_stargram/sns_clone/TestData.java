package com.pigeon_stargram.sns_clone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.PostsDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
}
