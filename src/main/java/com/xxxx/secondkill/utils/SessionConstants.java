package com.xxxx.secondkill.utils;

import java.time.Duration;

public final class SessionConstants {

    public static final String USER_SESSION_PREFIX = "user:";
    public static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);

    private SessionConstants() {
    }

    public static String userSessionKey(String ticket) {
        return USER_SESSION_PREFIX + ticket;
    }
}
