package com.pigeon_stargram.sns_clone.controller.file;

import com.pigeon_stargram.sns_clone.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @GetMapping("/{filename:.+}")
    public String getFile(@PathVariable String filename) {

        return fileService.loadFileAsPresignedUrl(filename);
    }
}
