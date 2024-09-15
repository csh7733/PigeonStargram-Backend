package com.pigeon_stargram.sns_clone.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JSON 변환을 위한 유틸리티 클래스입니다.
 *
 * 이 클래스는 객체를 JSON 문자열로 변환하거나, JSON 데이터를 특정 클래스 타입으로 변환하는 기능을 제공합니다.
 */
@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * 객체를 JSON 문자열로 변환하는 메서드입니다.
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    /**
     * 특정 객체를 지정된 타입으로 변환하는 메서드입니다.
     */
    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        try {
            return objectMapper.convertValue(fromValue, toValueType);
        } catch (Exception e) {
            throw new RuntimeException("Error converting value to " + toValueType.getName(), e);
        }
    }
}
