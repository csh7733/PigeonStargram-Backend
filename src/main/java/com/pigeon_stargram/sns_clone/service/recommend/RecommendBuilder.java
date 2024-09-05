package com.pigeon_stargram.sns_clone.service.recommend;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.user.response.ResponseRecommendUserInfoDto;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.UNSUPPORTED_OPERATION;

public class RecommendBuilder {
    private RecommendBuilder() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    public static ResponseRecommendUserInfoDto buildResponseRecommendUserInfoDto(User targetUser,String randomRecommendName,Integer size){

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
