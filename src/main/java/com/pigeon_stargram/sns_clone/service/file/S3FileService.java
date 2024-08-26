package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.exception.file.FileLoadException;
import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@Profile("local-s3")
public class S3FileService implements FileService{

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public List<String> saveFiles(List<MultipartFile> files) {
        // 게시글 작성에서 이미지없이 글만작성을 하는 경우를 위해서 필요
        if(files == null) return null;

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

            return filename;

        } catch (IOException e) {
            throw new FileUploadException(FILE_UPLOAD_FAIL, e);
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            InputStreamResource resource = new InputStreamResource(s3Client.getObject(getObjectRequest));

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileLoadException(FILE_NOT_FOUND_OR_UNREADABLE + filename);
            }
        } catch (Exception e) {
            throw new FileLoadException(FILE_NOT_FOUND_OR_UNREADABLE + filename, e);
        }
    }

    private static String getFilename(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
}
