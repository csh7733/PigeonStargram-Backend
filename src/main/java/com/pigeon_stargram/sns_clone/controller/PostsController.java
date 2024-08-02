package com.pigeon_stargram.sns_clone.controller;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pigeon_stargram.sns_clone.dto.PostsDto;
import com.pigeon_stargram.sns_clone.dto.PostsDtoList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RequestMapping("")
@RestController
public class PostsController {

    @GetMapping("/api/posts/list")
    public List<PostsDto> init() throws IOException {
        log.info("PostsController init");
        ObjectMapper objectMapper = new ObjectMapper();

        List<PostsDto> postsDtos = objectMapper.readValue(new ClassPathResource("data/posts.json").getFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, PostsDto.class));


        return postsDtos;
    }
}
