package com.xxxx.secondkill.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class SeckillAdmissionServiceIntegrationTest {

    private final long goodsId = ThreadLocalRandom.current().nextLong(9_000_000L, 9_999_999L);
    private final long userId = ThreadLocalRandom.current().nextLong(9_000_000L, 9_999_999L);

    @Autowired
    private SeckillAdmissionService admissionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void cleanUp() {
        redisTemplate.delete(Arrays.asList(
                "seckill:{" + goodsId + "}:stock",
                "seckill:{" + goodsId + "}:path:" + userId,
                "seckill:{" + goodsId + "}:status:" + userId,
                "seckill:{" + goodsId + "}:path-rate:" + userId
        ));
    }

    @Test
    void consumesDynamicPathAndPreventsDuplicateReservationAtomically() {
        admissionService.initializeStockIfAbsent(goodsId, 2);

        String firstPath = admissionService.createPath(userId, goodsId);
        assertNotNull(firstPath);
        assertEquals(SeckillAdmissionService.AdmissionResult.INVALID_PATH,
                admissionService.admit(userId, goodsId, "wrong-path"));
        assertEquals(SeckillAdmissionService.AdmissionResult.ACCEPTED,
                admissionService.admit(userId, goodsId, firstPath));

        String secondPath = admissionService.createPath(userId, goodsId);
        assertNotNull(secondPath);
        assertEquals(SeckillAdmissionService.AdmissionResult.DUPLICATE,
                admissionService.admit(userId, goodsId, secondPath));
        assertEquals("1", redisTemplate.opsForValue().get("seckill:{" + goodsId + "}:stock"));

        admissionService.releaseReservation(userId, goodsId);
        assertEquals("2", redisTemplate.opsForValue().get("seckill:{" + goodsId + "}:stock"));
        assertNull(admissionService.getCachedResult(userId, goodsId));
    }
}
