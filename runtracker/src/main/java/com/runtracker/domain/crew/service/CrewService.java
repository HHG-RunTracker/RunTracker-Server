package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.exception.AlreadyCrewMemberException;
import com.runtracker.domain.crew.exception.ApplicantNotFoundException;
import com.runtracker.domain.crew.exception.CrewAlreadyExistsException;
import com.runtracker.domain.crew.exception.CrewApplicationPendingException;
import com.runtracker.domain.crew.exception.CrewNotFoundException;
import com.runtracker.domain.crew.exception.MemberNotFoundException;
import com.runtracker.domain.crew.exception.NoPendingApplicationException;
import com.runtracker.domain.crew.exception.NotCrewLeaderException;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;

    public void createCrew(CrewCreateDTO.Request request, Long leaderId) {
        memberRepository.findById(leaderId)
                .orElseThrow(MemberNotFoundException::new);
        
        List<Crew> existingCrews = crewRepository.findByLeaderId(leaderId);
        if (!existingCrews.isEmpty()) {
            throw new CrewAlreadyExistsException();
        }
        
        Crew crew = request.toEntity(leaderId);
        crewRepository.save(crew);
        
        CrewMember crewLeader = CrewMember.builder()
                .crewId(crew.getId())
                .memberId(leaderId)
                .role(MemberRole.CREW_LEADER)
                .status(CrewMemberStatus.ACTIVE)
                .build();
        crewMemberRepository.save(crewLeader);
    }
    
    public void applyToJoinCrew(Long crewId, Long applicantId) {
        memberRepository.findById(applicantId)
                .orElseThrow(MemberNotFoundException::new);
        
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);
        
        CrewMember existingMembership = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, applicantId)
                .orElse(null);
                
        if (existingMembership != null) {
            if (existingMembership.getStatus() == CrewMemberStatus.ACTIVE) {
                throw new AlreadyCrewMemberException();
            }
            if (existingMembership.getStatus() == CrewMemberStatus.PENDING) {
                throw new CrewApplicationPendingException();
            }
        }
        
        CrewMember newApplication = CrewMember.builder()
                .crewId(crewId)
                .memberId(applicantId)
                .role(MemberRole.CREW_MEMBER)
                .status(CrewMemberStatus.PENDING)
                .build();
        crewMemberRepository.save(newApplication);
    }
    
    public void cancelCrewApplication(Long crewId, Long applicantId) {
        memberRepository.findById(applicantId)
                .orElseThrow(MemberNotFoundException::new);
        
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);
        
        CrewMember application = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, applicantId)
                .orElseThrow(NoPendingApplicationException::new);
        
        if (application.getStatus() != CrewMemberStatus.PENDING) {
            throw new NoPendingApplicationException();
        }
        
        crewMemberRepository.delete(application);
    }
    
    public void processJoinRequest(Long crewId, CrewApprovalDTO.Request request, Long leaderId) {
        validateCrewManagementPermission(crewId, leaderId);

        CrewMember applicant = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, request.getMemberId())
                .orElseThrow(ApplicantNotFoundException::new);
        
        if (applicant.getStatus() != CrewMemberStatus.PENDING) {
            throw new ApplicantNotFoundException();
        }
        
        if (request.getApproved()) {
            applicant.approve();
            crewMemberRepository.save(applicant);
        } else {
            crewMemberRepository.delete(applicant);
        }
    }
    
    private void validateCrewManagementPermission(Long crewId, Long memberId) {
        CrewMember member = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(NotCrewLeaderException::new);

        if (member.getRole() != MemberRole.CREW_LEADER) {
            throw new NotCrewLeaderException();
        }
    }
}