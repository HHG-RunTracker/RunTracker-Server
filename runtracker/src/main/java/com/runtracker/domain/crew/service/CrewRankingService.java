package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewRankingDTO;
import com.runtracker.domain.crew.dto.CrewRankingData;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewRanking;
import com.runtracker.domain.crew.entity.CrewRecord;
import com.runtracker.domain.crew.entity.CrewRunning;
import com.runtracker.domain.crew.repository.CrewRankingRepository;
import com.runtracker.domain.crew.repository.CrewRecordRepository;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewRunningRepository;
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
    private final CrewRecordRepository crewRecordRepository;
    private final CrewRepository crewRepository;
    private final CrewRunningRepository crewRunningRepository;

    /**
     * 랭킹 조회
     */
    public CrewRankingDTO.Response getDailyRanking(LocalDate date) {
        List<CrewRanking> rankings = findExistingRankings(date);
        
        if (rankings.isEmpty()) {
            rankingCalculation(date);
            rankings = findExistingRankings(date);
        }
        
        return buildResponse(date, rankings);
    }

    /**
     * 랭킹 강제 재계산
     */
    public void recalculateRanking(LocalDate date) {
        rankingCalculation(date);
    }

    /**
     * 크루 기록 추가 시 실시간 랭킹 업데이트
     */
    public void updateRankingForCrewRecord(Long crewId, LocalDate date, CrewRecord newRecord) {
        updateOrCreateCrewRanking(crewId, date, newRecord);
        reorderRankPositions(date);
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

        List<CrewRecord> records = getAllCrewRecordsUpToDate(date);

        for (CrewRecord record : records) {
            Long crewId = getCrewIdFromRecord(record);
            if (crewId != null) {
                crewDataMap.computeIfAbsent(crewId, CrewRankingData::new)
                        .addRecord(record);
            }
        }
        includeAllCrews(crewDataMap);
        
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

    private List<CrewRecord> getAllCrewRecordsUpToDate(LocalDate date) {
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<Long> crewRunningIds = crewRunningRepository.findAll().stream()
                .filter(cr -> !cr.getCreatedAt().isAfter(endOfDay))
                .map(CrewRunning::getId)
                .toList();
        
        return crewRecordRepository.findByCrewRunningIdIn(crewRunningIds);
    }

    private void includeAllCrews(Map<Long, CrewRankingData> crewDataMap) {
        crewRepository.findAll().forEach(crew -> 
            crewDataMap.putIfAbsent(crew.getId(), new CrewRankingData(crew.getId()))
        );
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
                !existing.getTotalRunningTime().equals(data.getTotalRunningTime()) ||
                !existing.getParticipantCount().equals(data.getParticipantCount());
        
        if (needsUpdate) {
            existing.updateRankPosition(rank);
            existing.updateTotalDistance(data.getTotalDistance());
            existing.updateTotalRunningTime(data.getTotalRunningTime());
            existing.updateParticipantCount(data.getParticipantCount());
            crewRankingRepository.save(existing);
        }
    }

    private void updateOrCreateCrewRanking(Long crewId, LocalDate date, CrewRecord newRecord) {
        Optional<CrewRanking> existingRanking = crewRankingRepository.findByDateAndCrewId(date, crewId);
        
        if (existingRanking.isPresent()) {
            existingRanking.get().addCrewRecord(newRecord.getDistance(), newRecord.getRunningTime());
            crewRankingRepository.save(existingRanking.get());
        } else {
            createNewRanking(date, crewId, newRecord);
        }
    }

    private void createNewRanking(LocalDate date, Long crewId, CrewRecord record) {
        CrewRanking newRanking = CrewRanking.builder()
                .date(date)
                .crewId(crewId)
                .rankPosition(1) // 임시 순위로 순위 지정 reorderRankPositions에서 추후 재정렬
                .totalDistance(record.getDistance())
                .totalRunningTime(record.getRunningTime())
                .participantCount(1)
                .build();
        crewRankingRepository.save(newRanking);
    }

    private void reorderRankPositions(LocalDate date) {
        List<CrewRanking> rankings = crewRankingRepository.findByDateOrderByTotalDistanceDesc(date);
        
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).updateRankPosition(i + 1);
        }
        
        crewRankingRepository.saveAll(rankings);
    }

    private Long getCrewIdFromRecord(CrewRecord record) {
        return crewRunningRepository.findById(record.getCrewRunningId())
                .map(CrewRunning::getCrewId)
                .orElse(null);
    }

    private CrewRankingDTO.CrewRankInfo convertToRankInfo(CrewRanking crewRanking) {
        Optional<Crew> crew = crewRepository.findById(crewRanking.getCrewId());
        
        return CrewRankingDTO.CrewRankInfo.builder()
                .crewId(crewRanking.getCrewId())
                .crewName(crew.map(Crew::getTitle).orElse("Unknown"))
                .crewPhoto(crew.map(Crew::getPhoto).orElse(null))
                .totalDistance(crewRanking.getTotalDistance())
                .totalRunningTime(crewRanking.getTotalRunningTime())
                .participantCount(crewRanking.getParticipantCount())
                .rank(crewRanking.getRankPosition())
                .build();
    }
}