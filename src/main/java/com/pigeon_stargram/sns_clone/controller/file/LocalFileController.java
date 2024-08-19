package com.pigeon_stargram.sns_clone.controller.file;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.service.file.FileService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class LocalFileController {

    private final PostsService postsService;
    private final FileService fileService;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                log.info("success");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.info("error1");
                throw new RuntimeException("File not found " + filename);
            }
        } catch (Exception e) {
            log.info("error2");
            throw new RuntimeException("File not found " + filename, e);
        }
    }

//    @PostMapping("/test")
//    public ResponseEntity<?> createPost(
//            @RequestPart("content") String content,
//            @RequestPart("images") List<MultipartFile> files) {
//
//        List<Image> images = fileService.saveFiles(files);
//
//        return ResponseEntity.ok(images);
//    }

}
