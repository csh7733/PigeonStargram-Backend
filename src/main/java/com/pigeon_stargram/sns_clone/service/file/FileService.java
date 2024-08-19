package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface FileService {
    List<Image> saveFiles(List<MultipartFile> files);
}
