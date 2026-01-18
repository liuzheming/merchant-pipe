package com.merchant.kernel.common.utils;

import com.merchant.kernel.common.constant.UserInfo;

public class UserManager {
    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();
    private static final UserInfo DEFAULT_USER = new UserInfo(0L, "system");

    private UserManager() {
    }

    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(UserInfo userInfo) {
        USER_HOLDER.set(userInfo);
    }

    public static void clear() {
        USER_HOLDER.remove();
    }

    public static UserInfo getDefaultUser() {
        return DEFAULT_USER;
    }

    public static String translateUcIdToUserCode(String ucId) {
        return ucId;
    }
}
