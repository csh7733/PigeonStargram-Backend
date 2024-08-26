package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.exception.file.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@Profile("local")
public class LocalFileService implements FileService {

    private final String uploadDir = "uploads/";

    @Override
    public List<String> saveFiles(List<MultipartFile> files) {
        List<String> images = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir);

        if(files == null) return null;

        for (MultipartFile file : files) {
            images.add(saveFile(file));
        }

        return images;
    }

    @Override
    public String saveFile(MultipartFile file) {
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);

            Files.copy(file.getInputStream(), filePath);

            return filename;
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("파일을 찾을 수 없거나 읽을 수 없습니다: " + filename);
            }
        } catch (IOException e) {
            throw new FileStorageException("파일을 불러오는 중 오류가 발생했습니다: " + filename, e);
        }
    }
}
