package com.ke.merchant.kernel.common.utils;

import com.ke.merchant.kernel.common.exception.ServiceException;

public class CheckUtil {
    private CheckUtil() {
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new ServiceException(message);
        }
    }
}
