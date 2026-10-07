package com.xxxx.secondkill.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CookieUtil {

    private static final int COOKIE_MAX_AGE = Math.toIntExact(SessionConstants.SESSION_TIMEOUT.getSeconds());

    private CookieUtil() {
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response,
                                 String name, String value) {
        Cookie cookie = new Cookie(name, value);
        String contextPath = request.getContextPath();
        cookie.setPath(contextPath == null || contextPath.isEmpty() ? "/" : contextPath);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }
}
