package com.pigeon_stargram.sns_clone.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

/**
 * LocalDateTime 관련 유틸리티 클래스입니다.
 *
 * 이 클래스는 LocalDateTime 객체의 포맷팅, 변환, 시간 비교 및 시간 관련 유틸리티 메서드를 제공합니다.
 */
public class LocalDateTimeUtil {

    // 날짜 및 시간 형식을 지정하는 포맷터
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter chatFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnnnnn");

    /**
     * LocalDateTime 객체를 기본 형식("yyyy-MM-dd HH:mm:ss")으로 문자열로 변환합니다.
     *
     * @param modifiedDate 변환할 LocalDateTime 객체
     * @return 변환된 날짜 및 시간 문자열
     */
    public static String formatTime(LocalDateTime modifiedDate) {
        return modifiedDate.format(formatter);
    }

    /**
     * LocalDateTime 객체를 채팅 형식("yyyy-MM-dd HH:mm:ss.nnnnnnnnn")으로 문자열로 변환합니다.
     *
     * @param modifiedDate 변환할 LocalDateTime 객체
     * @return 변환된 채팅 형식의 날짜 및 시간 문자열
     */
    public static String chatFormatTime(LocalDateTime modifiedDate) {
        return modifiedDate.format(chatFormatter);
    }

    /**
     * 주어진 문자열을 채팅 형식의 LocalDateTime으로 변환합니다.
     *
     * @param lastFetchedTime 변환할 날짜 문자열
     * @return 변환된 LocalDateTime 객체
     */
    public static LocalDateTime getChatLocalDateTime(String lastFetchedTime) {
        if (lastFetchedTime == null) return getCurrentTime();
        return LocalDateTime.parse(lastFetchedTime, chatFormatter);
    }

    /**
     * 현재 시간을 기본 형식("yyyy-MM-dd HH:mm:ss")으로 반환합니다.
     *
     * @return 현재 시간의 문자열 형식
     */
    public static String getCurrentFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        return formatTime(now);
    }

    /**
     * 현재 시간을 LocalDateTime 객체로 반환합니다.
     *
     * @return 현재 시간
     */
    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    /**
     * 주어진 타임스탬프(double)를 LocalDateTime 객체로 변환합니다.
     *
     * @param timestamp 변환할 타임스탬프
     * @return 변환된 LocalDateTime 객체
     */
    public static LocalDateTime convertDoubleToLocalDateTime(Double timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp.longValue()), ZoneId.systemDefault());
    }

    /**
     * 날짜 문자열을 역순으로 비교하는 Comparator를 반환합니다.
     *
     * @return 역순으로 정렬된 Comparator
     */
    public static Comparator<String> getReverseOrderComparator() {
        return Comparator.comparing(
                timeString -> LocalDateTime.parse(timeString, formatter),
                Comparator.reverseOrder()
        );
    }

    /**
     * 24시간 전의 시간을 반환합니다.
     *
     * @return 24시간 전의 LocalDateTime
     */
    public static LocalDateTime getExpirationTime() {
        return LocalDateTime.now().minusHours(24);
    }

    /**
     * 현재 시간을 밀리초 단위의 double로 반환합니다.
     *
     * @return 현재 시간의 밀리초 값
     */
    public static Double getCurrentTimeMillis() {
        return (double) System.currentTimeMillis();
    }

    /**
     * 주어진 LocalDateTime 객체를 밀리초 단위의 double로 변환합니다.
     *
     * @param time 변환할 LocalDateTime 객체
     * @return 변환된 밀리초 값
     */
    public static Double getTimeMillis(LocalDateTime time) {
        return (double) time.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * 주어진 LocalDateTime 객체를 UTC 기준의 초 단위 double 점수로 변환합니다.
     *
     * @param localDateTime 변환할 LocalDateTime 객체
     * @return 변환된 초 단위 점수(double)
     */
    public static Double convertToScore(LocalDateTime localDateTime) {
        return (double) localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
