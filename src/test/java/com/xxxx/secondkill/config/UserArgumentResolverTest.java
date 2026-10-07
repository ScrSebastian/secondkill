package com.xxxx.secondkill.config;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserArgumentResolverTest {

    @Test
    void resolvesUserFromTicketCookie() throws Exception {
        IUserService userService = mock(IUserService.class);
        UserArgumentResolver resolver = new UserArgumentResolver(userService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie("userTicket", "ticket-123"));
        User expectedUser = new User();
        when(userService.getUserByCookie("ticket-123", request, response)).thenReturn(expectedUser);

        Object actualUser = resolver.resolveArgument(
                null, null, new ServletWebRequest(request, response), null);

        assertSame(expectedUser, actualUser);
    }

    @Test
    void redirectsWhenUserSessionIsMissing() throws Exception {
        IUserService userService = mock(IUserService.class);
        UserArgumentResolver resolver = new UserArgumentResolver(userService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(userService.getUserByCookie(null, request, response)).thenReturn(null);

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        Object actualUser = resolver.resolveArgument(
                null, mavContainer, new ServletWebRequest(request, response), null);

        assertNull(actualUser);
        assertEquals("/login/toLogin", response.getRedirectedUrl());
        assertTrue(mavContainer.isRequestHandled());
    }
}
