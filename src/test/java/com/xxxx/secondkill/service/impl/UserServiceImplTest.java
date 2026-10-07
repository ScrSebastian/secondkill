package com.xxxx.secondkill.service.impl;

import com.xxxx.secondkill.mapper.UserMapper;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.utils.SessionConstants;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private static final String TICKET = "ticket-123";

    @Test
    void getUserByCookieRefreshesValidSession() {
        UserMapper userMapper = mock(UserMapper.class);
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        User expectedUser = new User();
        expectedUser.setId(13800138000L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(SessionConstants.userSessionKey(TICKET))).thenReturn(expectedUser);
        UserServiceImpl userService = new UserServiceImpl(userMapper, redisTemplate);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        User actualUser = userService.getUserByCookie(TICKET, request, response);

        assertSame(expectedUser, actualUser);
        verify(redisTemplate).expire(SessionConstants.userSessionKey(TICKET), SessionConstants.SESSION_TIMEOUT);
        Cookie cookie = response.getCookie("userTicket");
        assertEquals(TICKET, cookie.getValue());
        assertEquals(Math.toIntExact(SessionConstants.SESSION_TIMEOUT.getSeconds()), cookie.getMaxAge());
    }

    @Test
    void getUserByCookieRejectsBlankTicketWithoutRedisLookup() {
        UserMapper userMapper = mock(UserMapper.class);
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        UserServiceImpl userService = new UserServiceImpl(userMapper, redisTemplate);

        User user = userService.getUserByCookie(" ", new MockHttpServletRequest(), new MockHttpServletResponse());

        assertNull(user);
        verify(redisTemplate, never()).opsForValue();
    }
}
