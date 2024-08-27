package com.pigeon_stargram.sns_clone.worker;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
    private final ConcurrentHashMap<String, Integer> uploadProgressMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> uploadCompletionMap = new ConcurrentHashMap<>();

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public void uploadFileAsync(MultipartFile file, String filename, String fieldKey, int totalFiles) {
        // 파일 데이터를 미리 메모리로 로드
        byte[] fileData;
        try {
            log.info("미리 로드");
            fileData = file.getBytes();
        } catch (IOException e) {
            log.error("파일 데이터 로드 실패 - {}", filename, e);
            throw new FileUploadException(FILE_UPLOAD_FAIL, e);
        }

        // 업로드 시작 시 총 파일 수를 맵에 저장
        uploadProgressMap.putIfAbsent(fieldKey, totalFiles);
        uploadCompletionMap.putIfAbsent(fieldKey, false);  // 초기값은 false

        executorService.submit(() -> {
            log.info("비동기 중: 파일 업로드 시작 - {}", filename);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
            log.info("비동기 중: 파일 업로드 완료 - {}", filename);

            // 업로드가 완료된 후 맵에서 카운트를 감소시킴
            int remainingFiles = uploadProgressMap.merge(fieldKey, -1, Integer::sum);

            // 남은 파일이 0이면 마지막 파일이 업로드된 것으로 간주하고 isLast 처리
            if (remainingFiles == 0) {
                log.info("비동기 중: 모든 파일 업로드 완료 - {}", fieldKey);

                // Redis Hash에서 fieldKey에 해당하는 postId 가져오기
                Long postId = redisService.getValueFromHash("UPLOADING_POSTS_HASH", fieldKey, Long.class);

                if (postId != null) {
                    // Redis Set에서 해당 postId 제거
                    redisService.removeFromSet(UPLOADING_POSTS_SET, postId);
                    log.info("비동기 중: Redis에서 업로드 중인 Set에서 postId 제거 - {}", postId);
                }

                // 작업 완료 후 맵에서 해당 키 제거
                uploadProgressMap.remove(fieldKey);
                uploadCompletionMap.put(fieldKey, true);  // 업로드 완료 상태로 설정
            }
        });
    }

    // 업로드가 완료되었는지 확인하는 메서드
    public Boolean isUploadComplete(String fieldKey) {
        return uploadCompletionMap.getOrDefault(fieldKey, false);
    }
}
