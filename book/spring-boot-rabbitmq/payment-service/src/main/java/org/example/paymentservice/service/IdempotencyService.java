package org.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final long KEY_EXPIRATION_MINUTES = 10; // 키 유효시간

    private final StringRedisTemplate stringRedisTemplate;

    public boolean startProcessing(String key) {
        String redisKey = IDEMPOTENCY_KEY_PREFIX + key;

        // SetIfAbsent: 키가 없을 때만 설정
        Boolean isSet = stringRedisTemplate.opsForValue().setIfAbsent(
                redisKey,
                STATUS_PROCESSING,
                KEY_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        log.info("Idempotency Key: {} is set to: {}", redisKey, isSet);

        // Null을 대비해서 비교
        return Boolean.TRUE.equals(isSet);
    }

    public void setCompleted(String key) {
        String redisKey = IDEMPOTENCY_KEY_PREFIX + key;
        stringRedisTemplate.opsForValue().set(
                redisKey,
                STATUS_COMPLETED,
                KEY_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }
}
