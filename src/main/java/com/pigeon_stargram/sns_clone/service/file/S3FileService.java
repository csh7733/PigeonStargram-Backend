package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.FILE_UPLOAD_FAIL;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@Profile("local-s3")
public class S3FileService implements FileService{

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Override
    public List<String> saveFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::saveFile)
                .collect(Collectors.toList());
    }
    
    @Override
    public String saveFile(MultipartFile file) {
        String filename = getFilename(file);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, filename);

            return fileUrl;

        } catch (IOException e) {
            throw new FileUploadException(FILE_UPLOAD_FAIL, e);
        }
    }

    private static String getFilename(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
}
