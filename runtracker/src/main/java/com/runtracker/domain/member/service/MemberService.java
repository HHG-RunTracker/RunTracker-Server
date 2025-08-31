package com.runtracker.domain.member.service;

import com.runtracker.domain.member.service.dto.LoginTokenDto;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.RunningBackup;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.domain.member.repository.RunningBackupRepository;
import com.runtracker.domain.course.repository.CourseRepository;
import com.runtracker.domain.record.repository.RecordRepository;
import com.runtracker.domain.record.entity.RunningRecord;
import com.runtracker.domain.member.exception.MemberNotFoundException;
import com.runtracker.domain.member.exception.InvalidDifficultyException;
import com.runtracker.domain.member.exception.BackupNotFoundException;
import com.runtracker.domain.member.exception.BackupSerializationException;
import com.runtracker.domain.member.exception.BackupDeserializationException;
import com.runtracker.domain.member.exception.BackupAlreadyRestoredException;
import com.runtracker.domain.member.dto.MemberUpdateDTO;
import com.runtracker.domain.member.dto.NotificationSettingDTO;
import com.runtracker.domain.member.dto.RunningBackupDTO;
import com.runtracker.domain.member.enums.BackupType;
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
        
        Member newMember = Member.builder()
                .socialAttr(socialAttr)
                .socialId(socialId)
                .photo(photo)
                .name(name)
                .build();
        
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
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + memberId));
    }

    @Transactional
    public Member updateProfile(Long memberId, MemberUpdateDTO.Request request) {
        Member member = getMemberById(memberId);

        if (request.getDifficulty() != null) {
            validateDifficulty(request.getDifficulty());
        }
        
        member.updateProfile(
                request.getPhoto(),
                request.getName(),
                request.getIntroduce(),
                request.getAge(),
                request.getGender(),
                request.getRegion(),
                request.getDifficulty(),
                request.getSearchBlock(),
                request.getProfileBlock()
        );
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
                            .crewRunningId(record.getCrewRunningId())
                            .runningTime(record.getRunningTime())
                            .startedAt(record.getStartedAt())
                            .finishedAt(record.getFinishedAt())
                            .distance(record.getDistance())
                            .walk(record.getWalk())
                            .calorie(record.getCalorie())
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
                            .crewRunningId(backupRecord.getCrewRunningId())
                            .runningTime(backupRecord.getRunningTime())
                            .startedAt(backupRecord.getStartedAt())
                            .finishedAt(backupRecord.getFinishedAt())
                            .distance(backupRecord.getDistance())
                            .walk(backupRecord.getWalk())
                            .calorie(backupRecord.getCalorie())
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