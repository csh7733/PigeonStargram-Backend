package com.pigeon_stargram.sns_clone.service.file;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.dto.file.internal.FileUploadResultDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * 파일 업로드, 저장 및 읽기와 관련된 서비스 인터페이스입니다.
 * 파일을 서버에 저장하고, 파일을 미리 서명된 URL로 로드하는 등의 기능을 제공합니다.
 */
public interface FileService {

    /**
     * 여러 파일을 저장하고 결과를 반환합니다.
     *
     * @param files 저장할 파일 목록
     * @return 저장 결과를 담은 FileUploadResultDto 객체
     */
    FileUploadResultDto saveFiles(List<MultipartFile> files);

    /**
     * 단일 파일을 저장하고 저장된 파일의 경로를 반환합니다.
     *
     * @param file 저장할 파일
     * @return 저장된 파일의 경로
     */
    String saveFile(MultipartFile file);

    /**
     * 파일 이름을 기반으로 미리 서명된 URL을 생성하고 반환합니다.
     *
     * @param filename 미리 서명된 URL을 생성할 파일 이름
     * @return 미리 서명된 파일의 URL
     */
    String loadFileAsPresignedUrl(String filename);
}
