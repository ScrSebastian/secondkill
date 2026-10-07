package com.xxxx.secondkill.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CookieUtilTest {

    @Test
    void getsCookieValueByName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("userTicket", "ticket-123"));

        assertEquals("ticket-123", CookieUtil.getCookieValue(request, "userTicket"));
    }

    @Test
    void returnsNullWhenCookieIsMissing() {
        assertNull(CookieUtil.getCookieValue(new MockHttpServletRequest(), "userTicket"));
    }
}
