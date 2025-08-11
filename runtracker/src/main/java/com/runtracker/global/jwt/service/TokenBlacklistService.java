package com.runtracker.global.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    /**
     * 토큰을 블랙리스트에 추가
     */
    public void blacklistToken(String token, long expirationTime) {
        try {
            long currentTime = System.currentTimeMillis();
            long remainingTime = expirationTime - currentTime;
            
            if (remainingTime > 0) {
                String key = BLACKLIST_PREFIX + token;
                Duration ttl = Duration.ofMillis(remainingTime);
                redisTemplate.opsForValue().set(key, "blacklisted", ttl);
                log.info("Token blacklisted successfully with TTL: {} ms", remainingTime);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Failed to check token blacklist status", e);
            return false;
        }
    }

    /**
     * 특정 토큰을 블랙리스트에서 제거 (테스트용)
     */
    public void removeFromBlacklist(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Failed to remove token from blacklist", e);
        }
    }
}