package com.pigeon_stargram.sns_clone.dto.file.internal;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ToString
@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private MultipartFile file;
    private Long postId;
    private Boolean isLast;
}
