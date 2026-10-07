package com.xxxx.secondkill.service;

import com.xxxx.secondkill.utils.UUIDUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillAdmissionService {

    public enum AdmissionResult {
        ACCEPTED,
        SOLD_OUT,
        DUPLICATE,
        INVALID_PATH
    }

    private static final Duration PATH_TTL = Duration.ofSeconds(30);
    private static final Duration STATUS_TTL = Duration.ofHours(24);
    private static final int PATH_RATE_LIMIT = 5;
    private static final int PATH_RATE_WINDOW_SECONDS = 1;

    private static final DefaultRedisScript<Long> CREATE_PATH_SCRIPT = script(
            "lua/create_seckill_path.lua");
    private static final DefaultRedisScript<Long> ADMISSION_SCRIPT = script(
            "lua/seckill_admission.lua");
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = script(
            "lua/release_seckill_reservation.lua");
    private static final DefaultRedisScript<Long> FAIL_SCRIPT = script(
            "lua/fail_seckill_reservation.lua");

    private final StringRedisTemplate redisTemplate;

    public SeckillAdmissionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createPath(Long userId, Long goodsId) {
        String path = UUIDUtil.uuid();
        Long result = redisTemplate.execute(
                CREATE_PATH_SCRIPT,
                Arrays.asList(rateKey(userId, goodsId), pathKey(userId, goodsId)),
                String.valueOf(PATH_RATE_LIMIT),
                String.valueOf(PATH_RATE_WINDOW_SECONDS),
                path,
                String.valueOf(PATH_TTL.getSeconds())
        );
        return Long.valueOf(1L).equals(result) ? path : null;
    }

    public AdmissionResult admit(Long userId, Long goodsId, String path) {
        Long result = redisTemplate.execute(
                ADMISSION_SCRIPT,
                Arrays.asList(stockKey(goodsId), pathKey(userId, goodsId), statusKey(userId, goodsId)),
                path,
                String.valueOf(STATUS_TTL.getSeconds())
        );
        if (Long.valueOf(1L).equals(result)) {
            return AdmissionResult.ACCEPTED;
        }
        if (Long.valueOf(0L).equals(result)) {
            return AdmissionResult.SOLD_OUT;
        }
        if (Long.valueOf(2L).equals(result)) {
            return AdmissionResult.DUPLICATE;
        }
        return AdmissionResult.INVALID_PATH;
    }

    public void releaseReservation(Long userId, Long goodsId) {
        redisTemplate.execute(
                RELEASE_SCRIPT,
                Arrays.asList(stockKey(goodsId), statusKey(userId, goodsId))
        );
    }

    public void failAndReleaseReservation(Long userId, Long goodsId) {
        redisTemplate.execute(
                FAIL_SCRIPT,
                Arrays.asList(stockKey(goodsId), statusKey(userId, goodsId)),
                String.valueOf(STATUS_TTL.getSeconds())
        );
    }

    public void markSuccess(Long userId, Long goodsId, Long orderId) {
        redisTemplate.opsForValue().set(
                statusKey(userId, goodsId), "SUCCESS:" + orderId,
                STATUS_TTL.getSeconds(), TimeUnit.SECONDS);
    }

    public void markFailed(Long userId, Long goodsId) {
        redisTemplate.opsForValue().set(
                statusKey(userId, goodsId), "FAILED",
                STATUS_TTL.getSeconds(), TimeUnit.SECONDS);
    }

    public Long getCachedResult(Long userId, Long goodsId) {
        String status = redisTemplate.opsForValue().get(statusKey(userId, goodsId));
        if (status == null) {
            return null;
        }
        if ("PENDING".equals(status)) {
            return 0L;
        }
        if ("FAILED".equals(status)) {
            return -1L;
        }
        if (status.startsWith("SUCCESS:")) {
            try {
                return Long.valueOf(status.substring("SUCCESS:".length()));
            } catch (NumberFormatException ignored) {
                return 0L;
            }
        }
        return 0L;
    }

    public void initializeStockIfAbsent(Long goodsId, Integer stock) {
        if (goodsId == null || stock == null || stock < 0) {
            return;
        }
        redisTemplate.opsForValue().setIfAbsent(stockKey(goodsId), String.valueOf(stock));
    }

    private static DefaultRedisScript<Long> script(String path) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(path));
        script.setResultType(Long.class);
        return script;
    }

    private static String stockKey(Long goodsId) {
        return "seckill:{" + goodsId + "}:stock";
    }

    private static String pathKey(Long userId, Long goodsId) {
        return "seckill:{" + goodsId + "}:path:" + userId;
    }

    private static String statusKey(Long userId, Long goodsId) {
        return "seckill:{" + goodsId + "}:status:" + userId;
    }

    private static String rateKey(Long userId, Long goodsId) {
        return "seckill:{" + goodsId + "}:path-rate:" + userId;
    }
}
