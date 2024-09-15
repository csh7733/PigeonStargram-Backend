package com.pigeon_stargram.sns_clone.dto.recommend;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

/**
 * 추천 친구 DTO 변환기.
 * 이 클래스는 User 엔티티를 기반으로 추천 친구 정보를 담은 DTO를 생성하는 역할을 합니다.
 * 인스턴스화를 방지하기 위해 private 생성자와 UnsupportedOperationException을 던집니다.
 */
public class RecommendDtoConvertor {
    private RecommendDtoConvertor() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    /**
     * User 엔티티와 추가 정보 (랜덤 추천 이름, 팔로우 수)를 기반으로
     * 추천 친구 정보를 담은 ResponseRecommendUserInfoDto 객체를 생성합니다.
     *
     * @param targetUser           추천할 사용자 정보
     * @param randomRecommendName  해당 사용자를 팔로우하는 랜덤 사용자 이름
     * @param size                 해당 사용자를 팔로우하는 친구 수
     * @return 생성된 ResponseRecommendUserInfoDto 객체
     */
    public static ResponseRecommendUserInfoDto toResponseRecommendUserInfoDto(User targetUser, String randomRecommendName, Integer size){

        return  ResponseRecommendUserInfoDto.builder()
                .userId(targetUser.getId())
                .name(targetUser.getName())
                .avatar(targetUser.getAvatar())
                .company(targetUser.getCompany())
                .randomRecommendName(randomRecommendName)
                .total(size)
                .build();
    }
}
