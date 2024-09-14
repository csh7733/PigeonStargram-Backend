package com.pigeon_stargram.sns_clone.worker;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pigeon_stargram.sns_clone.exception.file.FileUploadException;
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


@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadWorker {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final RedisService redisService;
    private final S3Client s3Client;
    private final ConcurrentHashMap<String, Integer> uploadProgressMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> uploadCompletionMap = new ConcurrentHashMap<>();

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    /**
     * 파일을 비동기적으로 S3에 업로드하는 메서드
     *
     * @param file        업로드할 파일
     * @param filename    S3에 저장될 파일 이름
     * @param fieldKey    업로드 작업을 구분하는 고유 키
     * @param totalFiles  총 업로드할 파일 수
     */
    public void uploadFileAsync(MultipartFile file, String filename, String fieldKey, int totalFiles) {
        // 파일 데이터를 미리 메모리로 로드
        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            throw new FileUploadException(FILE_UPLOAD_FAIL, e); // 파일 로드 실패 시 예외 처리
        }

        // 업로드 시작 시 총 파일 수를 맵에 저장, 초기 완료 상태는 false
        uploadProgressMap.putIfAbsent(fieldKey, totalFiles);
        uploadCompletionMap.putIfAbsent(fieldKey, false);


        // 비동기 작업으로 S3에 파일 업로드 처리
        executorService.submit(() -> {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();


            // S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

            // 업로드가 완료된 후 남은 파일 수를 감소시킴
            int remainingFiles = uploadProgressMap.merge(fieldKey, -1, Integer::sum);

            // 남은 파일이 0이면 모든 파일이 업로드된 것으로 간주
            if (remainingFiles == 0) {
                // Redis에서 fieldKey에 해당하는 postId를 가져옴
                Long postId = redisService.getValueFromHash("UPLOADING_POSTS_HASH", fieldKey, Long.class);

                if (postId != null) {
                    // Redis의 업로드 진행 Set에서 해당 postId 제거
                    redisService.removeFromSet(UPLOADING_POSTS_SET, postId);
                }

                // 모든 파일이 업로드 완료되었으면 진행 상황 맵에서 제거하고 완료 상태로 설정
                uploadProgressMap.remove(fieldKey);
                uploadCompletionMap.put(fieldKey, true);
            }
        });
    }

    /**
     * 주어진 fieldKey에 해당하는 업로드 작업이 완료되었는지 확인하는 메서드
     */
    public Boolean isUploadComplete(String fieldKey) {
        return uploadCompletionMap.getOrDefault(fieldKey, false);
    }
}
