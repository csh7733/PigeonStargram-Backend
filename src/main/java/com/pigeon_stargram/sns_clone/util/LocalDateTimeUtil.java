package com.pigeon_stargram.sns_clone.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class LocalDateTimeUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatTime(LocalDateTime modifiedDate) {
        return modifiedDate.format(formatter);
    }

    public static String getCurrentFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        return formatTime(now);
    }

    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    public static Comparator<String> getReverseOrderComparator() {
        return Comparator.comparing(
                timeString -> LocalDateTime.parse(timeString, formatter),
                Comparator.reverseOrder()
        );
    }
}
