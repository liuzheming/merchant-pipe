package com.ke.merchant.kernel.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final String DATETIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final LocalDateTime DEFAULT_START_TIME = LocalDateTime.of(1970, 1, 2, 0, 0);

    private DateUtil() {
    }

    public static String localDateTimeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(DATETIMEFORMAT));
    }

    public static String localDateTimeToString(LocalDateTime time, String pattern) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }
}
