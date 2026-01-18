package com.ke.merchant.kernel.common.utils;

public class RedisKeyUtils {
    private RedisKeyUtils() {
    }

    public static String getKey(String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}
