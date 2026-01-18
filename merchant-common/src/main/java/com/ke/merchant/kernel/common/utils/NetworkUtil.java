package com.ke.merchant.kernel.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {
    private NetworkUtil() {
    }

    public static InetAddress getLocalHostExactAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
