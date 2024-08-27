package com.pigeon_stargram.sns_clone.dto.file.internal;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResultDto {
    private List<String> fileNames;
    private String hashKey;
}