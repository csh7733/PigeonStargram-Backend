package com.pigeon_stargram.sns_clone.dto.file.internal;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@ToString
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private String fileName;
    private byte[] fileData;
    private String hashKey;
    private Boolean isLast;

    public FileDto(MultipartFile file, String hashKey, boolean isLast) {
        try {
            this.fileName = file.getOriginalFilename();
            this.fileData = file.getBytes();
            this.hashKey = hashKey;
            this.isLast = isLast;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create FileDto from MultipartFile", e);
        }
    }
}
