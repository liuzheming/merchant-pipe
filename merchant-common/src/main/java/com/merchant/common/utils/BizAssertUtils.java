package com.merchant.common.utils;

import com.merchant.common.exception.ServiceException;

public class BizAssertUtils {
    private BizAssertUtils() {
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new ServiceException(message);
        }
    }

    public static void notEmpty(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new ServiceException(message);
        }
    }
}
