package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.exception.AlreadyCrewMemberException;
import com.runtracker.domain.crew.exception.AlreadyJoinedOtherCrewException;
import com.runtracker.domain.crew.exception.ApplicantNotFoundException;
import com.runtracker.domain.crew.exception.BannedFromCrewException;
import com.runtracker.domain.crew.exception.CannotKickCrewLeaderException;
import com.runtracker.domain.crew.exception.CannotKickManagerAsManagerException;
import com.runtracker.domain.crew.exception.CannotKickYourselfException;
import com.runtracker.domain.crew.exception.CannotModifyLeaderRoleException;
import com.runtracker.domain.crew.exception.CrewAlreadyExistsException;
import com.runtracker.domain.crew.exception.CrewApplicationPendingException;
import com.runtracker.domain.crew.exception.CrewNotFoundException;
import com.runtracker.domain.crew.exception.InvalidCrewRoleException;
import com.runtracker.domain.crew.exception.MemberNotFoundException;
import com.runtracker.domain.crew.exception.NoPendingApplicationException;
import com.runtracker.domain.crew.exception.NotCrewLeaderException;
import com.runtracker.domain.crew.exception.SameRoleUpdateException;
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

        List<CrewMember> activeCrewMemberships = crewMemberRepository
                .findByMemberIdAndStatus(applicantId, CrewMemberStatus.ACTIVE);
        if (!activeCrewMemberships.isEmpty()) {
            throw new AlreadyJoinedOtherCrewException();
        }
        
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
            if (existingMembership.getStatus() == CrewMemberStatus.BANNED) {
                throw new BannedFromCrewException();
            }
        }
        
        CrewMember newApplication = CrewMember.builder()
                .crewId(crewId)
                .memberId(applicantId)
                .role(MemberRole.USER)
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
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);
                
        validateCrewManagementPermission(crewId, leaderId);

        CrewMember applicant = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, request.getMemberId())
                .orElseThrow(ApplicantNotFoundException::new);
        
        if (applicant.getStatus() != CrewMemberStatus.PENDING) {
            throw new ApplicantNotFoundException();
        }
        
        if (request.getApproved()) {
            List<CrewMember> activeCrewMemberships = crewMemberRepository
                    .findByMemberIdAndStatus(request.getMemberId(), CrewMemberStatus.ACTIVE);
            if (!activeCrewMemberships.isEmpty()) {
                throw new AlreadyCrewMemberException();
            }

            applicant.approve();
            crewMemberRepository.save(applicant);

            List<CrewMember> pendingApplications = crewMemberRepository
                    .findByMemberIdAndStatus(request.getMemberId(), CrewMemberStatus.PENDING);

            pendingApplications.removeIf(member -> member.getCrewId().equals(crewId));
            crewMemberRepository.deleteAll(pendingApplications);
        } else {
            crewMemberRepository.delete(applicant);
        }
    }
    
    public void updateCrewMemberRole(Long crewId, CrewMemberUpdateDTO.Request request, Long managerId) {
        validateCrewManagementPermission(crewId, managerId);
        
        CrewMember targetMember = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, request.getMemberId())
                .orElseThrow(MemberNotFoundException::new);
        
        if (targetMember.getStatus() != CrewMemberStatus.ACTIVE) {
            throw new MemberNotFoundException();
        }

        if (targetMember.getRole() == MemberRole.CREW_LEADER) {
            throw new CannotModifyLeaderRoleException();
        }
        
        if (targetMember.getRole() == request.getRole()) {
            throw new SameRoleUpdateException();
        }
        
        if (!isValidCrewRole(request.getRole())) {
            throw new InvalidCrewRoleException();
        }
        
        targetMember.updateRole(request.getRole());
        crewMemberRepository.save(targetMember);
    }
    
    public void updateCrew(Long crewId, CrewUpdateDTO.Request request, Long leaderId) {
        validateCrewLeaderPermission(crewId, leaderId);
        
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);
        
        if (request.getTitle() != null) {
            crew.updateTitle(request.getTitle());
        }
        if (request.getPhoto() != null) {
            crew.updatePhoto(request.getPhoto());
        }
        if (request.getIntroduce() != null) {
            crew.updateIntroduce(request.getIntroduce());
        }
        if (request.getRegion() != null) {
            crew.updateRegion(request.getRegion());
        }
        if (request.getDifficulty() != null) {
            crew.updateDifficulty(request.getDifficulty());
        }
        
        crewRepository.save(crew);
    }
    
    public void deleteCrew(Long crewId, Long leaderId) {
        validateCrewLeaderPermission(crewId, leaderId);
        
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        List<CrewMember> crewMembers = crewMemberRepository.findByCrewId(crewId);
        crewMemberRepository.deleteAll(crewMembers);

        crewRepository.delete(crew);
    }
    
    public void banCrewMember(Long crewId, Long targetMemberId, Long managerId) {
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        validateCrewManagementPermission(crewId, managerId);

        if (targetMemberId.equals(managerId)) {
            throw new CannotKickYourselfException();
        }

        CrewMember targetMember = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, targetMemberId)
                .orElseThrow(MemberNotFoundException::new);

        if (targetMember.getStatus() != CrewMemberStatus.ACTIVE) {
            throw new MemberNotFoundException();
        }

        if (targetMember.getRole() == MemberRole.CREW_LEADER) {
            throw new CannotKickCrewLeaderException();
        }

        // 매니저 권한 체크 후 매니저가 매니저 추방 못하게 예외 처리
        CrewMember manager = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, managerId)
                .orElseThrow(NotCrewLeaderException::new);
        
        if (manager.getRole() == MemberRole.CREW_MANAGER && 
            targetMember.getRole() == MemberRole.CREW_MANAGER) {
            throw new CannotKickManagerAsManagerException();
        }

        targetMember.ban();
        crewMemberRepository.save(targetMember);
    }
    
    private boolean isValidCrewRole(MemberRole role) {
        return role == MemberRole.CREW_MEMBER || role == MemberRole.CREW_MANAGER;
    }
    
    private void validateCrewLeaderPermission(Long crewId, Long memberId) {
        CrewMember member = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(NotCrewLeaderException::new);

        if (member.getRole() != MemberRole.CREW_LEADER) {
            throw new NotCrewLeaderException();
        }
    }
    
    private void validateCrewManagementPermission(Long crewId, Long memberId) {
        CrewMember member = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(NotCrewLeaderException::new);

        if (member.getRole() != MemberRole.CREW_LEADER && member.getRole() != MemberRole.CREW_MANAGER) {
            throw new NotCrewLeaderException();
        }
    }
}