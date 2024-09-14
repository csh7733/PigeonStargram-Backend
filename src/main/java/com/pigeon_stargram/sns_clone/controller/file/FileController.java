package com.pigeon_stargram.sns_clone.controller.file;

import com.pigeon_stargram.sns_clone.service.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 파일과 관련된 API 요청을 처리하는 Controller 클래스입니다.
 * 파일을 미리 서명된 URL로 로드하는 작업을 수행할 수 있습니다.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    /**
     * 주어진 파일 이름을 기반으로 미리 서명된 URL을 생성하여 반환합니다.
     *
     * @param filename 미리 서명된 URL을 생성할 파일 이름
     * @return 미리 서명된 파일의 URL
     */
    @GetMapping("/{filename:.+}")
    public String getFile(@PathVariable String filename) {

        return fileService.loadFileAsPresignedUrl(filename);
    }
}
