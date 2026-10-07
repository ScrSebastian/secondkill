package com.xxxx.secondkill.utils;

import java.util.UUID;

public final class UUIDUtil {

    private UUIDUtil() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
