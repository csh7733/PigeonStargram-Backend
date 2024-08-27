package com.pigeon_stargram.sns_clone.worker;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
import com.pigeon_stargram.sns_clone.service.file.S3FileService;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static com.pigeon_stargram.sns_clone.constant.RedisPostConstants.UPLOADING_POSTS_SET;
import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.FILE_UPLOAD_FAIL;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUploadWorker {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final RedisService redisService;
    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public void uploadFileAsync(MultipartFile file, String filename, String fieldKey, boolean isLast) {
        executorService.submit(() -> {
            log.info("비동기 중: 파일 업로드 시작 - {}", filename);
            try {
                byte[] fileData = file.getBytes();

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filename)
                        .build();
                Thread.sleep(10000);
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
                log.info("비동기 중: 파일 업로드 완료 - {}", filename);

                if (isLast) {
                    log.info("비동기 중: 마지막 파일 업로드 완료 - {}", filename);

                    // Redis Hash에서 fieldKey에 해당하는 postId 가져오기
                    Long postId = redisService.getValueFromHash("UPLOADING_POSTS_HASH", fieldKey, Long.class);

                    if (postId != null) {
                        // Redis Set에서 해당 postId 제거
                        redisService.removeFromSet(UPLOADING_POSTS_SET, postId);
                        log.info("비동기 중: Redis에서 업로드 중인 Set에서 postId 제거 - {}", postId);
                    }
                }


            } catch (IOException e) {
                log.error("비동기 중: 파일 업로드 실패 - {}", filename, e);
                throw new FileUploadException(FILE_UPLOAD_FAIL, e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
