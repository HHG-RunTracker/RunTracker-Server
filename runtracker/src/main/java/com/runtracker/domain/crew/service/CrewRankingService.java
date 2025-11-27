package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewRankingCacheDTO;
import com.runtracker.domain.crew.dto.CrewRankingDTO;
import com.runtracker.domain.crew.dto.CrewRankingData;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewRanking;
import com.runtracker.domain.crew.repository.CrewRankingRepository;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CrewRankingService {

    private final CrewRankingRepository crewRankingRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final RecordRepository recordRepository;
    private final CrewRankingCacheService cacheService;

    /**
     * 랭킹 조회 (Cache-Aside 패턴)
     */
    public CrewRankingDTO.Response getDailyRanking(LocalDate date) {
        Map<Long, CrewRankingCacheDTO> cachedRanking = cacheService.getRankingFromCache(date);

        if (cachedRanking != null && !cachedRanking.isEmpty()) {
            return buildResponseFromCache(date, cachedRanking);
        }

        List<CrewRanking> rankings = findExistingRankings(date);

        if (rankings.isEmpty()) {
            rankingCalculation(date);
            rankings = findExistingRankings(date);
        }

        if (!rankings.isEmpty()) {
            cacheService.saveRankingToCache(date, rankings);
        }

        return buildResponse(date, rankings);
    }

    /**
     * 랭킹 강제 재계산
     */
    public void recalculateRanking(LocalDate date) {
        cacheService.invalidateCache(date);

        rankingCalculation(date);

        List<CrewRanking> rankings = findExistingRankings(date);
        if (!rankings.isEmpty()) {
            cacheService.saveRankingToCache(date, rankings);
        }
    }

    /**
     * 랭킹 계산
     */
    private void rankingCalculation(LocalDate date) {
        Map<Long, CrewRankingData> crewDataMap = calculateCrewRankingData(date);
        saveOrUpdateRankings(date, crewDataMap);
    }

    /**
     * 크루별 누적 데이터 계산
     */
    private Map<Long, CrewRankingData> calculateCrewRankingData(LocalDate date) {
        Map<Long, CrewRankingData> crewDataMap = new HashMap<>();

        includeAllCrews(crewDataMap);
        calculateCrewRanking(date, crewDataMap);

        return crewDataMap;
    }

    /**
     * 계산된 데이터로 랭킹 저장/업데이트
     */
    private void saveOrUpdateRankings(LocalDate date, Map<Long, CrewRankingData> crewDataMap) {
        Map<Long, CrewRanking> existingRankings = getExistingRankingsMap(date);
        
        List<CrewRankingData> sortedData = crewDataMap.values().stream()
                .sorted((a, b) -> Double.compare(b.getTotalDistance(), a.getTotalDistance()))
                .toList();

        for (int i = 0; i < sortedData.size(); i++) {
            CrewRankingData data = sortedData.get(i);
            int rank = i + 1;
            
            CrewRanking existing = existingRankings.get(data.getCrewId());
            
            if (existing == null) {
                createNewRanking(date, data, rank);
            } else {
                updateExistingRanking(existing, data, rank);
            }
        }
    }

    private List<CrewRanking> findExistingRankings(LocalDate date) {
        return crewRankingRepository.findByDateOrderByRankPosition(date);
    }

    /**
     * Redis 캐시 데이터로 Response 생성
     */
    private CrewRankingDTO.Response buildResponseFromCache(LocalDate date,
                                                           Map<Long, CrewRankingCacheDTO> cachedRanking) {
        List<Long> crewIds = new ArrayList<>(cachedRanking.keySet());

        Map<Long, Crew> crewMap = crewRepository.findAllById(crewIds).stream()
                .collect(Collectors.toMap(Crew::getId, crew -> crew));

        List<CrewRankingDTO.CrewRankInfo> rankInfos = new ArrayList<>();
        int rank = 1;

        for (Long crewId : crewIds) {
            CrewRankingCacheDTO data = cachedRanking.get(crewId);
            Crew crew = crewMap.get(crewId);

            rankInfos.add(CrewRankingDTO.CrewRankInfo.builder()
                    .crewId(crewId)
                    .crewName(crew != null ? crew.getTitle() : "Unknown")
                    .crewPhoto(crew != null ? crew.getPhoto() : null)
                    .totalDistance(data.getTotalDistance())
                    .totalRunningTime(data.getTotalRunningTime())
                    .rank(rank++)
                    .build());
        }

        return CrewRankingDTO.Response.builder()
                .date(date)
                .rankings(rankInfos)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private CrewRankingDTO.Response buildResponse(LocalDate date, List<CrewRanking> rankings) {
        List<CrewRankingDTO.CrewRankInfo> rankInfos = rankings.stream()
                .map(this::convertToRankInfo)
                .toList();

        LocalDateTime lastUpdated = rankings.isEmpty() ? LocalDateTime.now() :
                rankings.stream()
                        .map(CrewRanking::getUpdatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());

        return CrewRankingDTO.Response.builder()
                .date(date)
                .rankings(rankInfos)
                .lastUpdated(lastUpdated)
                .build();
    }

    private void includeAllCrews(Map<Long, CrewRankingData> crewDataMap) {
        crewRepository.findAll().forEach(crew ->
            crewDataMap.putIfAbsent(crew.getId(), new CrewRankingData(crew.getId()))
        );
    }

    /**
     * 크루원들의 개인 러닝 기록을 통해 크루별 통계 계산
     */
    private void calculateCrewRanking(LocalDate date, Map<Long, CrewRankingData> crewDataMap) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        for (Long crewId : crewDataMap.keySet()) {
            CrewRankingData crewData = crewDataMap.get(crewId);

            List<Long> memberIds = crewMemberRepository.findMemberIdsByCrewId(crewId);

            if (!memberIds.isEmpty()) {
                var records = recordRepository.findByMemberIdInAndCreatedAtBetweenAndFinishedAtIsNotNull(
                    memberIds, startOfDay, endOfDay);

                for (var record : records) {
                    if (record.getDistance() != null && record.getRunningTime() != null) {
                        crewData.addRecord(record.getDistance(), record.getRunningTime());
                    }
                }
            }
        }
    }

    private Map<Long, CrewRanking> getExistingRankingsMap(LocalDate date) {
        return crewRankingRepository.findByDateOrderByRankPosition(date).stream()
                .collect(Collectors.toMap(CrewRanking::getCrewId, ranking -> ranking));
    }

    private void createNewRanking(LocalDate date, CrewRankingData data, int rank) {
        CrewRanking newRanking = CrewRanking.builder()
                .date(date)
                .crewId(data.getCrewId())
                .rankPosition(rank)
                .totalDistance(data.getTotalDistance())
                .totalRunningTime(data.getTotalRunningTime())
                .participantCount(data.getParticipantCount())
                .build();
        crewRankingRepository.save(newRanking);
    }

    private void updateExistingRanking(CrewRanking existing, CrewRankingData data, int rank) {
        boolean needsUpdate = existing.getRankPosition() != rank ||
                Math.abs(existing.getTotalDistance() - data.getTotalDistance()) > 0.01 ||
                !existing.getTotalRunningTime().equals(data.getTotalRunningTime());

        if (needsUpdate) {
            existing.updateRankPosition(rank);
            existing.updateTotalDistance(data.getTotalDistance());
            existing.updateTotalRunningTime(data.getTotalRunningTime());
            crewRankingRepository.save(existing);
        }
    }

    private CrewRankingDTO.CrewRankInfo convertToRankInfo(CrewRanking crewRanking) {
        Optional<Crew> crew = crewRepository.findById(crewRanking.getCrewId());

        return CrewRankingDTO.CrewRankInfo.builder()
                .crewId(crewRanking.getCrewId())
                .crewName(crew.map(Crew::getTitle).orElse("Unknown"))
                .crewPhoto(crew.map(Crew::getPhoto).orElse(null))
                .totalDistance(crewRanking.getTotalDistance())
                .totalRunningTime(crewRanking.getTotalRunningTime())
                .rank(crewRanking.getRankPosition())
                .build();
    }
}