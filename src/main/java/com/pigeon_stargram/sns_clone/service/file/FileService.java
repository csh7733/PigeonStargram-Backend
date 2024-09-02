package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface FileService {
    FileUploadResultDto saveFiles(List<MultipartFile> files);
    String saveFile(MultipartFile file);
    public String loadFileAsPresignedUrl(String filename);
}
