package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewRankingCacheDTO;
import com.runtracker.domain.crew.entity.CrewRanking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewRankingCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RANKING_KEY_PREFIX = "crew:ranking:";
    private static final long CACHE_TTL_DAYS = 1;

    /**
     * 랭킹 데이터를 Redis에 저장
     */
    public void saveRankingToCache(LocalDate date, List<CrewRanking> rankings) {
        String key = getRankingKey(date);

        try {
            redisTemplate.delete(key);

            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

            for (CrewRanking ranking : rankings) {
                String member = ranking.getCrewId() + ":" + ranking.getTotalRunningTime();
                zSetOps.add(key, member, ranking.getTotalDistance());
            }

            redisTemplate.expire(key, CACHE_TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Failed to save crew ranking to cache: date={}", date, e);
        }
    }

    /**
     * Redis에서 랭킹 데이터 조회
     */
    public Map<Long, CrewRankingCacheDTO> getRankingFromCache(LocalDate date) {
        String key = getRankingKey(date);

        try {
            ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

            Set<ZSetOperations.TypedTuple<String>> rankingsWithScores =
                    zSetOps.reverseRangeWithScores(key, 0, -1);

            if (rankingsWithScores == null || rankingsWithScores.isEmpty()) {
                log.debug("No ranking cache found for date: {}", date);
                return null;
            }

            Map<Long, CrewRankingCacheDTO> result = new LinkedHashMap<>();

            for (ZSetOperations.TypedTuple<String> tuple : rankingsWithScores) {
                String member = tuple.getValue();
                Double score = tuple.getScore();

                if (member != null && score != null) {
                    String[] parts = member.split(":");
                    Long crewId = Long.parseLong(parts[0]);
                    Integer totalRunningTime = Integer.parseInt(parts[1]);

                    result.put(crewId, new CrewRankingCacheDTO(score, totalRunningTime));
                }
            }

            return result;

        } catch (Exception e) {
            log.error("Failed to get crew ranking from cache: date={}", date, e);
            return null;
        }
    }

    /**
     * 캐시 무효화
     */
    public void invalidateCache(LocalDate date) {
        String key = getRankingKey(date);
        redisTemplate.delete(key);
    }

    /**
     * Redis 키 생성
     */
    private String getRankingKey(LocalDate date) {
        return RANKING_KEY_PREFIX + date.toString();
    }
}