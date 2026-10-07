package com.xxxx.secondkill.config;

import com.xxxx.secondkill.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class RedisTemplateIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void serializesUser() {
        String key = "test:user:" + UUID.randomUUID();
        User expectedUser = new User();
        expectedUser.setId(13800138000L);
        expectedUser.setNickname("redis-test");
        expectedUser.setLastLoginDate(new Date());

        try {
            redisTemplate.opsForValue().set(key, expectedUser, Duration.ofMinutes(1));

            User actualUser = (User) redisTemplate.opsForValue().get(key);
            assertNotNull(actualUser);
            assertEquals(expectedUser.getId(), actualUser.getId());
            assertEquals(expectedUser.getNickname(), actualUser.getNickname());
            assertEquals(expectedUser.getLastLoginDate(), actualUser.getLastLoginDate());
        } finally {
            redisTemplate.delete(key);
        }
    }
}
