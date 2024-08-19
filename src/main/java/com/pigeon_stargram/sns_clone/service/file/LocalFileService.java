package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.exception.file.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class LocalFileService implements FileService {

    private final String uploadDir = "uploads/";

    @Override
    public List<Image> saveFiles(List<MultipartFile> files) {
        List<Image> images = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir);

        for (MultipartFile file : files) {
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir + filename);

                Files.copy(file.getInputStream(), filePath);

                images.add(new Image(filename, false));
            } catch (IOException e) {
                throw new FileStorageException("Failed to save file: " + file.getOriginalFilename(), e);
            }
        }

        return images;
    }
}
