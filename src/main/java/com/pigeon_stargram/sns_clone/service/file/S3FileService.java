package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import com.pigeon_stargram.sns_clone.exception.file.FileLoadException;
import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import com.pigeon_stargram.sns_clone.worker.FileUploadWorker;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.FILE_UPLOAD_QUEUE;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@Profile("local-s3")
public class S3FileService implements FileService{

    private final S3Client s3Client;
    private final RedisService redisService;
    private final FileUploadWorker fileUploadWorker;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public FileUploadResultDto saveFiles(List<MultipartFile> files) {
        if (files == null) return new FileUploadResultDto();

        String fieldKey = generateFieldKey();
        int totalFiles = files.size();
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = getFilename(file);
            fileNames.add(fileName);
            fileUploadWorker.uploadFileAsync(file, fileName, fieldKey, totalFiles);
        }

        return new FileUploadResultDto(fileNames, fieldKey);
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
            throw new FileLoadException(MALFORMED_URL + filename, e);
        }
    }

    private static String getFilename(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }

    private static String generateFieldKey() {
        return UUID.randomUUID().toString();
    }

}
