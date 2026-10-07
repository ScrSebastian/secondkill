package com.xxxx.secondkill.utils;

public final class ValidatorUtil {

    private static final String MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    private ValidatorUtil() {
    }

    public static boolean isMobile(String mobile) {
        return mobile != null && mobile.matches(MOBILE_PATTERN);
    }
}
