package com.runtracker.domain.member.service;

import com.runtracker.domain.member.entity.FcmToken;
import com.runtracker.domain.member.repository.FcmTokenRepository;
import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.RunningBackup;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.member.repository.RunningBackupRepository;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.crew.repository.CrewMemberRankingRepository;
import com.runtracker.domain.community.repository.PostRepository;
import com.runtracker.domain.community.repository.CommentRepository;
import com.runtracker.domain.community.repository.PostLikeRepository;
import com.runtracker.domain.schedule.repository.ScheduleRepository;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.member.exception.MemberNotFoundException;
import com.runtracker.domain.member.exception.InvalidDifficultyException;
import com.runtracker.domain.member.exception.InvalidMapStyleException;
import com.runtracker.domain.member.exception.BackupNotFoundException;
import com.runtracker.domain.member.exception.BackupSerializationException;
import com.runtracker.domain.member.exception.BackupDeserializationException;
import com.runtracker.domain.member.exception.BackupAlreadyRestoredException;
import com.runtracker.domain.member.dto.MemberUpdateDTO;
import com.runtracker.domain.member.dto.MemberCreateDTO;
import com.runtracker.domain.member.dto.NotificationSettingDTO;
import com.runtracker.domain.member.dto.RunningBackupDTO;
import com.runtracker.domain.member.dto.RunningSettingDTO;
import com.runtracker.domain.member.enums.BackupType;
import com.runtracker.domain.member.enums.MapStyle;
import com.runtracker.domain.course.enums.Difficulty;
import com.runtracker.global.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final RecordRepository recordRepository;
    private final RunningBackupRepository backupRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewMemberRankingRepository crewMemberRankingRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Transactional
    public Member createOrUpdateMember(String socialAttr, String socialId,
                                      String photo, String name) {
        Optional<Member> existingMember = memberRepository.findBySocialId(socialId);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            if (photo != null) {
                member.updatePhoto(photo);
            }
            return member;
        }

        MemberCreateDTO memberCreateDTO = MemberCreateDTO.builder()
                .socialAttr(socialAttr)
                .socialId(socialId)
                .photo(photo)
                .name(name)
                .build();

        Member newMember = new Member(memberCreateDTO);
        return memberRepository.save(newMember);
    }


    public Member getMemberByName(String name) {
        return memberRepository.findByName(name)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with name: " + name));
    }

    public Member getMemberBySocialId(String socialId) {
        return memberRepository.findBySocialId(socialId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with socialId: " + socialId));
    }

    @Transactional(readOnly = true)
    public LoginTokenDto.MemberSearchResult findMemberByName(String name) {
        Member member = getMemberByName(name);
        
        log.info("find member by name - userId: {}, socialId: {}", member.getId(), member.getSocialId());
        
        return LoginTokenDto.MemberSearchResult.builder()
                .userId(member.getId())
                .socialId(member.getSocialId())
                .build();
    }

    @Transactional
    public void logout(Long memberId) {
        // 현재 요청에서 토큰 추출
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    jwtUtil.blacklistToken(token);
                } catch (Exception e) {
                    log.error("Failed to blacklist token for user: {}", memberId, e);
                }
            }
        }

        removeFcmToken(memberId);
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));
    }

    @Transactional
    public Member updateProfile(Long memberId, MemberUpdateDTO.Request memberUpdateDTO) {
        Member member = getMemberById(memberId);

        if (memberUpdateDTO.getDifficulty() != null) {
            validateDifficulty(memberUpdateDTO.getDifficulty());
        }
        
        member.updateProfile(memberUpdateDTO);
        return member;
    }
    
    private void validateDifficulty(String difficulty) {
        try {
            Difficulty.valueOf(difficulty);
        } catch (IllegalArgumentException e) {
            throw new InvalidDifficultyException("Invalid difficulty value. Must be one of: EASY, MEDIUM, HARD");
        }
    }

    @Transactional
    public void updateNotificationSetting(Long memberId, NotificationSettingDTO.Request request) {
        Member member = getMemberById(memberId);
        member.updateNotificationSetting(request.getNotifyBlock());
    }

    @Transactional(readOnly = true)
    public RunningSettingDTO.Response getRunningSetting(Long memberId) {
        Member member = getMemberById(memberId);
        return RunningSettingDTO.Response.from(member);
    }

    @Transactional
    public void updateRunningSetting(Long memberId, RunningSettingDTO.Request runningSettingDTO) {
        Member member = getMemberById(memberId);

        if (runningSettingDTO.getPreferredDifficulty() != null) {
            validateDifficulty(runningSettingDTO.getPreferredDifficulty());
        }

        if (runningSettingDTO.getMapStyle() != null) {
            validateMapStyle(runningSettingDTO.getMapStyle());
        }

        member.updateRunningSetting(runningSettingDTO);
    }

    private void validateMapStyle(String mapStyle) {
        try {
            MapStyle.valueOf(mapStyle);
        } catch (IllegalArgumentException e) {
            throw new InvalidMapStyleException("Invalid map style value. Must be one of: STANDARD, SATELLITE, HYBRID");
        }
    }

    @Transactional
    public void updateFcmToken(Long memberId, String token) {
        Optional<FcmToken> existingToken = fcmTokenRepository.findByMemberId(memberId);

        if (existingToken.isPresent()) {
            existingToken.get().updateToken(token);
        } else {
            FcmToken newToken = FcmToken.builder()
                    .memberId(memberId)
                    .token(token)
                    .build();
            fcmTokenRepository.save(newToken);
        }
    }

    @Transactional
    public void removeFcmToken(Long memberId) {
        fcmTokenRepository.findByMemberId(memberId)
                    .ifPresent(token -> token.updateToken(null));
    }

    public Optional<String> getFcmToken(Long memberId) {
        return fcmTokenRepository.findTokenByMemberId(memberId);
    }

    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    jwtUtil.blacklistToken(token);
                } catch (Exception e) {
                    log.error("Failed to blacklist token for withdrawing user: {}", memberId, e);
                }
            }
        }

        fcmTokenRepository.deleteByMemberId(memberId);
        postLikeRepository.deleteByMemberId(memberId);
        commentRepository.deleteByMemberId(memberId);
        postRepository.deleteByMemberId(memberId);
        crewMemberRankingRepository.deleteByMemberId(memberId);
        crewMemberRepository.deleteByMemberId(memberId);
        crewRepository.deleteByLeaderId(memberId);
        scheduleRepository.deleteByMemberId(memberId);
        recordRepository.deleteByMemberId(memberId);
        backupRepository.deleteByMemberId(memberId);
        courseRepository.deleteByMemberId(memberId);
        memberRepository.delete(member);
    }

    @Transactional
    public void createBackup(Long memberId) {
        try {
            Member member = getMemberById(memberId);
            List<RunningRecord> runningRecords = recordRepository.findByMemberId(memberId);
            
            RunningBackupDTO.MemberBackupData memberBackupData = RunningBackupDTO.MemberBackupData.builder()
                    .id(member.getId())
                    .name(member.getName())
                    .socialId(member.getSocialId())
                    .build();
            
            List<RunningBackupDTO.RecordBackupData> recordBackupDataList = runningRecords.stream()
                    .map(record -> RunningBackupDTO.RecordBackupData.builder()
                            .id(record.getId())
                            .memberId(record.getMemberId())
                            .courseId(record.getCourseId())
                            .runningTime(record.getRunningTime())
                            .startedAt(record.getStartedAt())
                            .finishedAt(record.getFinishedAt())
                            .distance(record.getDistance())
                            .avgPace(record.getAvgPace())
                            .avgSpeed(record.getAvgSpeed())
                            .kcal(record.getKcal())
                            .walkCnt(record.getWalkCnt())
                            .avgHeartRate(record.getAvgHeartRate())
                            .maxHeartRate(record.getMaxHeartRate())
                            .avgCadence(record.getAvgCadence())
                            .maxCadence(record.getMaxCadence())
                            .path(record.getUserFinishLocation())
                            .segmentPaces(record.getSegmentPaces())
                            .segmentPaths(record.getSegmentPaths())
                            .createdAt(record.getCreatedAt())
                            .updatedAt(record.getUpdatedAt())
                            .build())
                    .toList();
            
            RunningBackupDTO.BackupData backupData = RunningBackupDTO.BackupData.builder()
                    .member(memberBackupData)
                    .runningRecords(recordBackupDataList)
                    .build();
            
            String backupDataJson = objectMapper.writeValueAsString(backupData);
            
            Optional<RunningBackup> existingBackup = backupRepository.findByMemberId(memberId);
            
            if (existingBackup.isPresent()) {
                existingBackup.get().updateBackupData(backupDataJson);
            } else {
                RunningBackup newBackup = RunningBackup.builder()
                        .memberId(memberId)
                        .backupData(backupDataJson)
                        .backupType(BackupType.ORIGINAL)
                        .build();
                backupRepository.save(newBackup);
            }
            
        } catch (JsonProcessingException e) {
            throw new BackupSerializationException("Failed to serialize backup data for member: " + memberId);
        } catch (Exception e) {
            throw new BackupSerializationException("Failed to create backup for member: " + memberId);
        }
    }

    @Transactional
    public void restoreRunningRecords(Long memberId) {
        Optional<RunningBackup> backup = backupRepository.findByMemberId(memberId);
        
        if (backup.isEmpty()) {
            throw new BackupNotFoundException("No backup found for member: " + memberId);
        }
        
        RunningBackup backupEntity = backup.get();
        
        if (BackupType.RESTORED.equals(backupEntity.getBackupType())) {
            throw new BackupAlreadyRestoredException("This backup has already been restored");
        }
        
        try {
            RunningBackupDTO.BackupData backupData = objectMapper.readValue(
                    backup.get().getBackupData(), 
                    new TypeReference<>() {}
            );
            
            List<RunningRecord> currentRecords = recordRepository.findByMemberId(memberId);
            
            for (RunningBackupDTO.RecordBackupData backupRecord : backupData.getRunningRecords()) {
                boolean recordExists = currentRecords.stream()
                        .anyMatch(current -> current.getId().equals(backupRecord.getId()));
                
                if (!recordExists) {
                    RunningRecord newRecord = RunningRecord.builder()
                            .memberId(backupRecord.getMemberId())
                            .courseId(backupRecord.getCourseId())
                            .runningTime(backupRecord.getRunningTime())
                            .startedAt(backupRecord.getStartedAt())
                            .finishedAt(backupRecord.getFinishedAt())
                            .distance(backupRecord.getDistance())
                            .avgPace(backupRecord.getAvgPace())
                            .avgSpeed(backupRecord.getAvgSpeed())
                            .kcal(backupRecord.getKcal())
                            .walkCnt(backupRecord.getWalkCnt())
                            .avgHeartRate(backupRecord.getAvgHeartRate())
                            .maxHeartRate(backupRecord.getMaxHeartRate())
                            .avgCadence(backupRecord.getAvgCadence())
                            .maxCadence(backupRecord.getMaxCadence())
                            .userFinishLocation(backupRecord.getPath())
                            .segmentPaces(backupRecord.getSegmentPaces())
                            .segmentPaths(backupRecord.getSegmentPaths())
                            .build();
                    recordRepository.save(newRecord);
                }
            }

            backupEntity.markAsRestored();
            
        } catch (JsonProcessingException e) {
            throw new BackupDeserializationException("Failed to deserialize backup data for member: " + memberId);
        }
    }

    @Transactional(readOnly = true)
    public RunningBackupDTO.BackupInfo getBackupInfo(Long memberId) {
        Optional<RunningBackup> backup = backupRepository.findByMemberId(memberId);
        
        if (backup.isEmpty()) {
            throw new BackupNotFoundException("No backup found for member: " + memberId);
        }
        
        RunningBackup backupEntity = backup.get();
        
        try {
            RunningBackupDTO.BackupData backupData = objectMapper.readValue(
                    backupEntity.getBackupData(), 
                    new TypeReference<>() {}
            );
            
            return RunningBackupDTO.BackupInfo.builder()
                    .backupId(backupEntity.getId())
                    .backupType(backupEntity.getBackupType().name())
                    .recordCount(backupData.getRunningRecords().size())
                    .updatedAt(backupEntity.getUpdatedAt().toString())
                    .build();
        } catch (JsonProcessingException e) {
            throw new BackupDeserializationException("Failed to parse backup data for member: " + memberId);
        }
    }
}