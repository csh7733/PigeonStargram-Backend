package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdateOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.user.internal.UpdatePasswordDto;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseUserInfoDto;

import java.util.List;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 Service 인터페이스.
 * 사용자 등록, 조회, 상태 업데이트 등을 위한 메서드를 제공합니다.
 */
public interface UserService {

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param id 사용자 ID
     * @return 조회된 사용자 정보
     */
    User getUserById(Long id);

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     * @param name 사용자 이름
     * @return 조회된 사용자 정보
     */
    User getUserByName(String name);

    /**
     * 사용자의 이메일(work email)로 사용자 정보를 조회합니다.
     * @param email 사용자 이메일
     * @return 조회된 사용자 정보
     */
    User getUserByWorkEmail(String email);

    /**
     * 이메일과 비밀번호로 사용자 정보를 조회합니다.
     * 로그인 처리 시 사용됩니다.
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 이메일과 비밀번호가 일치하는 사용자 정보
     */
    User getUserByWorkEmailAndPassword(String email, String password);

    /**
     * 사용자 조회 쿼리로 사용자를 검색합니다.
     * @param searchQuery 검색어
     * @return 검색어에 일치하는 사용자 목록
     */
    List<User> findBySearchQuery(String searchQuery);

    /**
     * 사용자 ID 목록으로 사용자 정보를 조회하여 DTO 목록으로 반환합니다.
     * @param userIds 사용자 ID 목록
     * @return 사용자 정보를 담은 ResponseUserInfoDto 리스트
     */
    List<ResponseUserInfoDto> getUserInfosByUserIds(List<Long> userIds);

    /**
     * 사용자 ID로 채팅 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 조회된 사용자 채팅 정보 (닉네임, 프로필 이미지 등)
     */
    ResponseUserChatDto getUserChatById(Long userId);

    /**
     * 사용자 ID로 온라인 상태 정보를 조회하여 DTO로 반환합니다.
     * @param id 사용자 ID
     * @return 온라인 상태 정보를 담은 ResponseOnlineStatusDto
     */
    ResponseOnlineStatusDto getOnlineStatus(Long id);

    /**
     * 회원가입 요청 정보로 새로운 사용자(User)를 생성하고 저장합니다.
     * @param dto 회원가입 요청 정보 DTO
     * @return 저장된 사용자 객체
     */
    User save(RequestRegisterDto dto);

    /**
     * 사용자의 온라인 상태를 업데이트합니다.
     * 상태 변경 후 캐시 및 데이터베이스에 반영합니다.
     * @param dto 온라인 상태 갱신 요청 정보 DTO
     * @return 상태가 갱신된 사용자 객체
     */
    User updateOnlineStatus(UpdateOnlineStatusDto dto);


    /**
     * 사용자의 비밀번호를 재설정합니다.
     * @param dto 비밀번호 재설정 요청 정보 DTO
     * @return 비밀번호가 재설정된 사용자 객체
     */
    User updatePassword(UpdatePasswordDto dto);

    /**
     * 사용자의 온라인 상태 변경 내용을 Redis를 통해 채팅 파트너에게 전송합니다.
     * @param dto 온라인 상태 갱신 요청 정보 DTO
     */
    void handleOnlineStatusUpdate(UpdateOnlineStatusDto dto);
}
