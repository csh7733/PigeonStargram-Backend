package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import com.pigeon_stargram.sns_clone.exception.file.FileLoadException;
import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import com.pigeon_stargram.sns_clone.worker.file.FileUploadWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Profile("local-s3")
public class FileServiceV2 implements FileService{

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final FileUploadWorker fileUploadWorker;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public FileUploadResultDto saveFiles(List<MultipartFile> files) {
        // 파일 리스트가 null인 경우 빈 결과를 반환
        if (files == null) return new FileUploadResultDto();

        // 고유한 필드 키 생성
        String fieldKey = generateFieldKey();
        int totalFiles = files.size();
        List<String> fileNames = new ArrayList<>();

        // 각 파일에 대해 비동기 업로드 처리
        for (MultipartFile file : files) {
            String fileName = getFilename(file);
            fileNames.add(fileName);
            // 비동기 방식으로 파일 업로드
            fileUploadWorker.uploadFileAsync(file, fileName, fieldKey, totalFiles);
        }

        // 업로드된 파일들의 이름과 필드 키를 포함한 결과 반환
        return new FileUploadResultDto(fileNames, fieldKey);
    }

    @Override
    public String saveFile(MultipartFile file) {
        // 파일 이름을 생성
        String filename = getFilename(file);

        try {
            // S3에 파일을 업로드하기 위한 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            // S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 이름 반환
            return filename;

        } catch (IOException e) {
            throw new FileUploadException(FILE_UPLOAD_FAIL, e);
        }
    }

    @Override
    public String loadFileAsPresignedUrl(String filename) {
        return generatePresignedUrl(filename);
    }

    private String generatePresignedUrl(String filename) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(10))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();

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
