package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewMemberRankingDTO;
import com.runtracker.domain.crew.dto.MemberRankingData;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.entity.CrewMemberRanking;
import com.runtracker.domain.crew.entity.CrewRunning;
import com.runtracker.domain.crew.repository.*;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CrewMemberRankingService {

    private final CrewMemberRankingRepository crewMemberRankingRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final CrewRunningRepository crewRunningRepository;
    private final RecordRepository recordRepository;
    private final MemberRepository memberRepository;

    /**
     * 특정 크루의 멤버 랭킹 조회
     */
    public CrewMemberRankingDTO.Response getCrewMemberRanking(Long crewId, LocalDate date) {
        List<CrewMemberRanking> rankings = findExistingMemberRankings(crewId, date);
        
        if (rankings.isEmpty()) {
            calculateCrewMemberRanking(crewId, date);
            rankings = findExistingMemberRankings(crewId, date);
        }
        
        return buildMemberRankingResponse(crewId, date, rankings);
    }

    /**
     * 특정 크루의 멤버 랭킹 강제 재계산
     */
    public void recalculateCrewMemberRanking(Long crewId, LocalDate date) {
        calculateCrewMemberRanking(crewId, date);
    }

    /**
     * 크루 멤버 랭킹 계산
     */
    private void calculateCrewMemberRanking(Long crewId, LocalDate date) {
        Map<Long, MemberRankingData> memberDataMap = calculateMemberRankingData(crewId, date);
        saveOrUpdateMemberRankings(crewId, date, memberDataMap);
    }

    /**
     * 크루 내 멤버별 누적 데이터 계산
     */
    private Map<Long, MemberRankingData> calculateMemberRankingData(Long crewId, LocalDate date) {
        Map<Long, MemberRankingData> memberDataMap = new HashMap<>();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<CrewRunning> crewRunnings = crewRunningRepository.findAll().stream()
                .filter(cr -> cr.getCrewId().equals(crewId))
                .filter(cr -> !cr.getCreatedAt().isAfter(endOfDay))
                .toList();
        
        List<Long> crewRunningIds = crewRunnings.stream()
                .map(CrewRunning::getId)
                .toList();

        List<RunningRecord> memberRecords = recordRepository.findAll().stream()
                .filter(record -> crewRunningIds.contains(record.getCrewRunningId()))
                .toList();

        for (RunningRecord record : memberRecords) {
            memberDataMap.computeIfAbsent(record.getMemberId(), MemberRankingData::new)
                    .addRecord(record.getDistance(), record.getRunningTime());
        }

        includeAllCrewMembers(crewId, memberDataMap);
        
        return memberDataMap;
    }

    /**
     * 계산된 데이터로 멤버 랭킹 저장
     */
    private void saveOrUpdateMemberRankings(Long crewId, LocalDate date, Map<Long, MemberRankingData> memberDataMap) {
        Map<Long, CrewMemberRanking> existingRankings = getExistingMemberRankingsMap(crewId, date);
        
        List<MemberRankingData> sortedData = memberDataMap.values().stream()
                .sorted((a, b) -> Double.compare(b.getTotalDistance(), a.getTotalDistance()))
                .toList();

        for (int i = 0; i < sortedData.size(); i++) {
            MemberRankingData data = sortedData.get(i);
            int rank = i + 1;
            
            CrewMemberRanking existing = existingRankings.get(data.getMemberId());
            
            if (existing == null) {
                createNewMemberRanking(crewId, date, data, rank);
            } else {
                updateExistingMemberRanking(existing, data, rank);
            }
        }
    }

    private List<CrewMemberRanking> findExistingMemberRankings(Long crewId, LocalDate date) {
        return crewMemberRankingRepository.findByCrewIdAndDateOrderByRankPosition(crewId, date);
    }

    private CrewMemberRankingDTO.Response buildMemberRankingResponse(Long crewId, LocalDate date, List<CrewMemberRanking> rankings) {
        List<CrewMemberRankingDTO.MemberRankInfo> rankInfos = rankings.stream()
                .map(this::convertToMemberRankInfo)
                .toList();

        Optional<Crew> crew = crewRepository.findById(crewId);
        LocalDateTime lastUpdated = rankings.isEmpty() ? LocalDateTime.now() : 
                rankings.stream()
                        .map(CrewMemberRanking::getUpdatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.now());

        return CrewMemberRankingDTO.Response.builder()
                .date(date)
                .crewId(crewId)
                .crewName(crew.map(Crew::getTitle).orElse("Unknown"))
                .rankings(rankInfos)
                .lastUpdated(lastUpdated)
                .build();
    }

    private void includeAllCrewMembers(Long crewId, Map<Long, MemberRankingData> memberDataMap) {
        crewMemberRepository.findByCrewId(crewId).stream()
                .filter(crewMember -> !crewMember.getStatus().equals(com.runtracker.domain.crew.enums.CrewMemberStatus.BANNED))
                .forEach(crewMember ->
                        memberDataMap.putIfAbsent(crewMember.getMemberId(), new MemberRankingData(crewMember.getMemberId()))
                );
    }

    private Map<Long, CrewMemberRanking> getExistingMemberRankingsMap(Long crewId, LocalDate date) {
        return crewMemberRankingRepository.findByCrewIdAndDateOrderByRankPosition(crewId, date).stream()
                .collect(Collectors.toMap(CrewMemberRanking::getMemberId, ranking -> ranking));
    }

    private void createNewMemberRanking(Long crewId, LocalDate date, MemberRankingData data, int rank) {
        CrewMemberRanking newRanking = CrewMemberRanking.builder()
                .date(date)
                .crewId(crewId)
                .memberId(data.getMemberId())
                .rankPosition(rank)
                .totalDistance(data.getTotalDistance())
                .totalRunningTime(data.getTotalRunningTime())
                .participationCount(data.getParticipationCount())
                .build();
        crewMemberRankingRepository.save(newRanking);
    }

    private void updateExistingMemberRanking(CrewMemberRanking existing, MemberRankingData data, int rank) {
        boolean needsUpdate = !existing.getRankPosition().equals(rank) ||
                Math.abs(existing.getTotalDistance() - data.getTotalDistance()) > 0.01 ||
                !existing.getTotalRunningTime().equals(data.getTotalRunningTime()) ||
                !existing.getParticipationCount().equals(data.getParticipationCount());
        
        if (needsUpdate) {
            existing.updateRankPosition(rank);
            existing.updateTotalData(data.getTotalDistance(), data.getTotalRunningTime(), data.getParticipationCount());
            crewMemberRankingRepository.save(existing);
        }
    }

    private CrewMemberRankingDTO.MemberRankInfo convertToMemberRankInfo(CrewMemberRanking ranking) {
        Optional<Member> member = memberRepository.findById(ranking.getMemberId());
        
        double averageDistance = ranking.getParticipationCount() > 0 ? 
                ranking.getTotalDistance() / ranking.getParticipationCount() : 0.0;
        int averageRunningTime = ranking.getParticipationCount() > 0 ? 
                ranking.getTotalRunningTime() / ranking.getParticipationCount() : 0;
        
        return CrewMemberRankingDTO.MemberRankInfo.builder()
                .memberId(ranking.getMemberId())
                .memberName(member.map(Member::getName).orElse("Unknown"))
                .memberPhoto(member.map(Member::getPhoto).orElse(null))
                .rank(ranking.getRankPosition())
                .totalDistance(ranking.getTotalDistance())
                .totalRunningTime(ranking.getTotalRunningTime())
                .participationCount(ranking.getParticipationCount())
                .averageDistance(averageDistance)
                .averageRunningTime(averageRunningTime)
                .build();
    }
}