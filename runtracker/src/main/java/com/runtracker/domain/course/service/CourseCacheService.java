package com.runtracker.domain.course.service;

import com.runtracker.domain.course.dto.CourseDetailDTO;
import com.runtracker.domain.course.service.dto.SlotData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String NEARBY_SLOTS_HASH = "nearby_slots";
    private static final String COURSE_DETAIL_HASH = "course_details";
    private static final Duration NEARBY_COURSES_TTL = Duration.ofHours(3);
    private static final Duration COURSE_DETAIL_TTL = Duration.ofHours(3);
    private static final double COORDINATE_ROUND_UNIT = 0.005; // 500m
    private static final int MAX_NEARBY_SLOTS = 10;
    private static final int MAX_COURSE_DETAIL_SLOTS = 200;

    /**
     * 좌표를 그리드로 변환 (500m 그리드)
     */
    private String generateLocationKey(Double latitude, Double longitude) {
        double gridLat = Math.floor(latitude / COORDINATE_ROUND_UNIT) * COORDINATE_ROUND_UNIT;
        double gridLng = Math.floor(longitude / COORDINATE_ROUND_UNIT) * COORDINATE_ROUND_UNIT;
        return String.format("%.3f:%.3f", gridLat, gridLng);
    }

    /**
     * 근처 코스 ID 리스트 캐싱 (Hash 사용)
     */
    public void cacheNearbyCourses(Double latitude, Double longitude, List<Long> courseIds) {
        try {
            String groupKey = generateLocationKey(latitude, longitude);

            SlotData slot = (SlotData) redisTemplate.opsForHash().get(NEARBY_SLOTS_HASH, groupKey);

            if (slot != null) {
                slot.updateAccess();
            } else {
                // 슬롯 개수 확인
                Long slotCount = redisTemplate.opsForHash().size(NEARBY_SLOTS_HASH);
                if (slotCount != null && slotCount >= MAX_NEARBY_SLOTS) {
                    evictLeastUsedSlot();
                }

                slot = SlotData.builder()
                        .location(groupKey)
                        .courseIds(new ArrayList<>(courseIds))
                        .lastAccessTime(LocalDateTime.now())
                        .frequency(1)
                        .build();
            }

            // Hash에 단일 키 업데이트 (O(1))
            redisTemplate.opsForHash().put(NEARBY_SLOTS_HASH, groupKey, slot);
            redisTemplate.expire(NEARBY_SLOTS_HASH, NEARBY_COURSES_TTL);

            log.debug("Cached nearby courses for group: {}, frequency: {}", groupKey, slot.getFrequency());
        } catch (Exception e) {
            log.warn("Failed to cache nearby courses for lat: {}, lng: {}, error: {}",
                    latitude, longitude, e.getMessage());
        }
    }

    /**
     * 근처 코스 ID 리스트 조회 (Hash 사용)
     */
    public List<Long> getNearbyCourseIds(Double latitude, Double longitude) {
        try {
            String groupKey = generateLocationKey(latitude, longitude);
            SlotData slot = (SlotData) redisTemplate.opsForHash().get(NEARBY_SLOTS_HASH, groupKey);

            if (slot != null) {
                slot.updateAccess();
                redisTemplate.opsForHash().put(NEARBY_SLOTS_HASH, groupKey, slot);
                return slot.getCourseIds();
            }

            return null;
        } catch (Exception e) {
            log.warn("Failed to get nearby course IDs from cache for lat: {}, lng: {}, error: {}",
                    latitude, longitude, e.getMessage());
            return null;
        }
    }

    /**
     * LRU + LFU 기반 슬롯 삭제 (Hash 사용)
     */
    private void evictLeastUsedSlot() {
        try {
            Map<Object, Object> allSlots = redisTemplate.opsForHash().entries(NEARBY_SLOTS_HASH);

            SlotData leastUsed = allSlots.values().stream()
                    .filter(obj -> obj instanceof SlotData)
                    .map(obj -> (SlotData) obj)
                    .min(Comparator
                            .comparing(SlotData::getFrequency)
                            .thenComparing(SlotData::getLastAccessTime))
                    .orElse(null);

            if (leastUsed != null) {
                redisTemplate.opsForHash().delete(NEARBY_SLOTS_HASH, leastUsed.getLocation());
                log.debug("Evicted least used slot: location={}, frequency={}",
                        leastUsed.getLocation(), leastUsed.getFrequency());
            }
        } catch (Exception e) {
            log.warn("Failed to evict least used slot: {}", e.getMessage());
        }
    }

    /**
     * 새 코스 추가 시 슬롯에 코스 ID 추가 (Hash 사용)
     */
    public void addCourseToSlot(Double latitude, Double longitude, Long courseId) {
        try {
            String groupKey = generateLocationKey(latitude, longitude);
            SlotData slot = (SlotData) redisTemplate.opsForHash().get(NEARBY_SLOTS_HASH, groupKey);

            if (slot != null) {
                slot.addCourseId(courseId);
                redisTemplate.opsForHash().put(NEARBY_SLOTS_HASH, groupKey, slot);
                log.debug("Added course {} to slot {}", courseId, groupKey);
            } else {
                log.debug("No slot found for group {} to add course {}", groupKey, courseId);
            }
        } catch (Exception e) {
            log.warn("Failed to add course to slot for lat: {}, lng: {}, courseId: {}, error: {}",
                    latitude, longitude, courseId, e.getMessage());
        }
    }

    /**
     * 코스 상세 정보 캐싱 (Hash 사용)
     */
    public void cacheCourseDetail(Long courseId, CourseDetailDTO detail) {
        try {
            // 슬롯 개수 확인
            Long slotCount = redisTemplate.opsForHash().size(COURSE_DETAIL_HASH);
            if (slotCount != null && slotCount >= MAX_COURSE_DETAIL_SLOTS) {
                evictOldestCourseDetailSlot();
            }

            redisTemplate.opsForHash().put(COURSE_DETAIL_HASH, courseId.toString(), detail);
            redisTemplate.expire(COURSE_DETAIL_HASH, COURSE_DETAIL_TTL);

            log.debug("Cached course detail for courseId: {}", courseId);
        } catch (Exception e) {
            log.warn("Failed to cache course detail for courseId: {}, error: {}", courseId, e.getMessage());
        }
    }

    /**
     * 코스 상세 정보 조회 (Hash 사용)
     */
    public CourseDetailDTO getCourseDetail(Long courseId) {
        try {
            Object detail = redisTemplate.opsForHash().get(COURSE_DETAIL_HASH, courseId.toString());

            if (detail instanceof CourseDetailDTO courseDetail) {
                log.debug("Cache hit for course detail, courseId: {}", courseId);
                return courseDetail;
            }

            log.debug("Cache miss for course detail, courseId: {}", courseId);
            return null;
        } catch (Exception e) {
            log.warn("Failed to get course detail from cache for courseId: {}, error: {}", courseId, e.getMessage());
            return null;
        }
    }

    /**
     * 다중 코스 상세 정보 조회 (multiGet 사용 - 성능 최적화)
     */
    public Map<Long, CourseDetailDTO> getMultipleCourseDetails(List<Long> courseIds) {
        try {
            List<Object> keys = courseIds.stream()
                    .map(String::valueOf)
                    .map(Object.class::cast)
                    .toList();

            List<Object> values = redisTemplate.opsForHash().multiGet(COURSE_DETAIL_HASH, keys);
            Map<Long, CourseDetailDTO> result = new HashMap<>();

            for (int i = 0; i < courseIds.size(); i++) {
                if (values.get(i) instanceof CourseDetailDTO detail) {
                    result.put(courseIds.get(i), detail);
                }
            }

            log.debug("Multi-get cache hit for {} courses", result.size());
            return result;
        } catch (Exception e) {
            log.warn("Failed to get multiple course details: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 가장 오래된 코스 상세 슬롯 삭제
     */
    private void evictOldestCourseDetailSlot() {
        try {
            Map<Object, Object> allDetails = redisTemplate.opsForHash().entries(COURSE_DETAIL_HASH);

            if (!allDetails.isEmpty()) {
                // 첫 번째 항목 삭제 (FIFO)
                Object firstKey = allDetails.keySet().iterator().next();
                redisTemplate.opsForHash().delete(COURSE_DETAIL_HASH, firstKey);
                log.debug("Evicted oldest course detail slot: {}", firstKey);
            }
        } catch (Exception e) {
            log.warn("Failed to evict oldest course detail slot: {}", e.getMessage());
        }
    }
}