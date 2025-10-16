package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewApprovalDTO;
import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.dto.CrewDetailDTO;
import com.runtracker.domain.crew.dto.CrewListDTO;
import com.runtracker.domain.crew.dto.CrewManagementDTO;
import com.runtracker.domain.crew.dto.CrewMemberUpdateDTO;
import com.runtracker.domain.crew.dto.CrewUpdateDTO;
import com.runtracker.domain.crew.dto.MemberProfileDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.event.CrewJoinRequestEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestCancelEvent;
import com.runtracker.domain.crew.event.CrewJoinRequestApprovalEvent;
import com.runtracker.domain.crew.event.CrewMemberRoleUpdateEvent;
import com.runtracker.domain.crew.event.CrewDeleteEvent;
import com.runtracker.domain.crew.event.CrewBanEvent;
import com.runtracker.domain.crew.event.CrewLeaveEvent;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.exception.AlreadyCrewMemberException;
import com.runtracker.domain.crew.exception.AlreadyJoinedOtherCrewException;
import com.runtracker.domain.crew.exception.ApplicantNotFoundException;
import com.runtracker.domain.crew.exception.BannedFromCrewException;
import com.runtracker.domain.crew.exception.CannotKickCrewLeaderException;
import com.runtracker.domain.crew.exception.CannotKickManagerAsManagerException;
import com.runtracker.domain.crew.exception.CannotKickYourselfException;
import com.runtracker.domain.crew.exception.CannotLeaveAsCrewLeaderException;
import com.runtracker.domain.crew.exception.CannotModifyLeaderRoleException;
import com.runtracker.domain.crew.exception.CrewAlreadyExistsException;
import com.runtracker.domain.crew.exception.CrewApplicationPendingException;
import com.runtracker.domain.crew.exception.CrewNotFoundException;
import com.runtracker.domain.crew.exception.CrewSearchResultNotFoundException;
import com.runtracker.domain.crew.exception.InvalidCrewRoleException;
import com.runtracker.domain.crew.exception.MemberNotFoundException;
import com.runtracker.domain.crew.exception.NoPendingApplicationException;
import com.runtracker.domain.crew.exception.SameRoleUpdateException;
import com.runtracker.domain.crew.repository.CrewRepository;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.global.util.ImageUpload;
import com.runtracker.global.security.UserDetailsImpl;
import com.runtracker.global.security.CrewAuthorizationUtil;
import com.runtracker.global.jwt.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CrewService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final CrewAuthorizationUtil authorizationUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageUpload imageUploadHelper;

    public void createCrew(CrewCreateDTO.Request request, UserDetailsImpl userDetails) {
        memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        List<Crew> existingCrews = crewRepository.findByLeaderId(userDetails.getMemberId());
        if (!existingCrews.isEmpty()) {
            throw new CrewAlreadyExistsException();
        }

        if (request.getPhoto() != null) {
            String photoUrl = imageUploadHelper.convertBase64ToUrlIfNeeded(request.getPhoto());
            CrewCreateDTO.Request updatedRequest = CrewCreateDTO.Request.builder()
                    .title(request.getTitle())
                    .photo(photoUrl)
                    .introduce(request.getIntroduce())
                    .region(request.getRegion())
                    .difficulty(request.getDifficulty())
                    .build();
            Crew crew = updatedRequest.toEntity(userDetails.getMemberId());
            crewRepository.save(crew);

            CrewMember crewLeader = CrewMember.builder()
                    .crewId(crew.getId())
                    .memberId(userDetails.getMemberId())
                    .role(MemberRole.CREW_LEADER)
                    .status(CrewMemberStatus.ACTIVE)
                    .build();
            crewMemberRepository.save(crewLeader);
        } else {
            Crew crew = request.toEntity(userDetails.getMemberId());
            crewRepository.save(crew);

            CrewMember crewLeader = CrewMember.builder()
                    .crewId(crew.getId())
                    .memberId(userDetails.getMemberId())
                    .role(MemberRole.CREW_LEADER)
                    .status(CrewMemberStatus.ACTIVE)
                    .build();
            crewMemberRepository.save(crewLeader);
        }
    }
    
    public void applyToJoinCrew(Long crewId, UserDetailsImpl userDetails) {
        memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);
        
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        List<CrewMember> activeCrewMemberships = crewMemberRepository
                .findByMemberIdAndStatus(userDetails.getMemberId(), CrewMemberStatus.ACTIVE);
        if (!activeCrewMemberships.isEmpty()) {
            throw new AlreadyJoinedOtherCrewException();
        }
        
        CrewMember existingMembership = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, userDetails.getMemberId())
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
                .memberId(userDetails.getMemberId())
                .role(MemberRole.USER)
                .status(CrewMemberStatus.PENDING)
                .build();
        crewMemberRepository.save(newApplication);

        List<CrewMember> managementMembers = getManagementMembers(crewId);

        for (CrewMember managementMember : managementMembers) {
            eventPublisher.publishEvent(new CrewJoinRequestEvent(
                userDetails.getMemberId(),
                managementMember.getMemberId(),
                crewId
            ));
        }
    }
    
    public void cancelCrewApplication(Long crewId, UserDetailsImpl userDetails) {
        memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        CrewMember application = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, userDetails.getMemberId())
                .orElseThrow(NoPendingApplicationException::new);

        if (application.getStatus() != CrewMemberStatus.PENDING) {
            throw new NoPendingApplicationException();
        }

        crewMemberRepository.delete(application);

        List<CrewMember> managementMembers = getManagementMembers(crewId);

        for (CrewMember managementMember : managementMembers) {
            eventPublisher.publishEvent(new CrewJoinRequestCancelEvent(
                userDetails.getMemberId(),
                managementMember.getMemberId(),
                crewId
            ));
        }
    }
    
    public void processJoinRequest(Long crewId, CrewApprovalDTO.Request request, UserDetailsImpl userDetails) {
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

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

            tokenBlacklistService.invalidateUserTokens(request.getMemberId());
        } else {
            crewMemberRepository.delete(applicant);
        }

        eventPublisher.publishEvent(new CrewJoinRequestApprovalEvent(
            request.getMemberId(),
            crewId,
            request.getApproved()
        ));
    }
    
    public void updateCrewMemberRole(Long crewId, CrewMemberUpdateDTO.Request request, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);
        
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

        tokenBlacklistService.invalidateUserTokens(request.getMemberId());

        eventPublisher.publishEvent(new CrewMemberRoleUpdateEvent(
            request.getMemberId(),
            crewId,
            request.getRole()
        ));
    }
    
    public void updateCrew(Long crewId, CrewUpdateDTO.Request request, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewLeaderPermission(userDetails, crewId);

        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        if (request.getTitle() != null) {
            crew.updateTitle(request.getTitle());
        }
        if (request.getPhoto() != null) {
            String photoUrl = imageUploadHelper.convertBase64ToUrlIfNeeded(request.getPhoto());
            crew.updatePhoto(photoUrl);
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
    
    public void deleteCrew(Long crewId, UserDetailsImpl userDetails) {
        authorizationUtil.validateCrewLeaderPermission(userDetails, crewId);

        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        List<CrewMember> crewMembers = crewMemberRepository.findByCrewId(crewId);

        List<Long> memberIdsToNotify = crewMembers.stream()
                .filter(member -> member.getStatus() == CrewMemberStatus.ACTIVE)
                .map(CrewMember::getMemberId)
                .toList();

        eventPublisher.publishEvent(new CrewDeleteEvent(memberIdsToNotify, crew.getTitle()));

        List<Long> memberIds = crewMembers.stream()
                .map(CrewMember::getMemberId)
                .toList();
        tokenBlacklistService.invalidateCrewMemberTokens(crewId, memberIds);

        crewMemberRepository.deleteAll(crewMembers);
        crewRepository.delete(crew);
    }
    
    public void banCrewMember(Long crewId, Long targetMemberId, UserDetailsImpl userDetails) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);

        if (targetMemberId.equals(userDetails.getMemberId())) {
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

        boolean isManager = userDetails.getRoles().contains(MemberRole.CREW_MANAGER) &&
                           !userDetails.getRoles().contains(MemberRole.CREW_LEADER);

        if (isManager && targetMember.getRole() == MemberRole.CREW_MANAGER) {
            throw new CannotKickManagerAsManagerException();
        }

        eventPublisher.publishEvent(new CrewBanEvent(targetMemberId, crew.getTitle()));

        targetMember.ban();
        crewMemberRepository.save(targetMember);

        tokenBlacklistService.invalidateUserTokens(targetMemberId);
    }
    
    public void leaveCrew(Long crewId, UserDetailsImpl userDetails) {
        Member leavingMember = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        CrewMember crewMember = crewMemberRepository
                .findByCrewIdAndMemberId(crewId, userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        if (crewMember.getStatus() != CrewMemberStatus.ACTIVE) {
            throw new MemberNotFoundException();
        }

        if (crewMember.getRole() == MemberRole.CREW_LEADER) {
            throw new CannotLeaveAsCrewLeaderException();
        }

        List<CrewMember> managementMembers = getManagementMembers(crewId);
        List<Long> managerIds = managementMembers.stream()
                .map(CrewMember::getMemberId)
                .toList();

        eventPublisher.publishEvent(new CrewLeaveEvent(managerIds, leavingMember.getName()));

        crewMemberRepository.delete(crewMember);

        tokenBlacklistService.invalidateUserTokens(userDetails.getMemberId());
    }
    
    @Transactional(readOnly = true)
    public CrewListDTO.ListResponse getAllCrews() {
        List<Crew> crews = crewRepository.findAll();
        return convertToCrewListResponse(crews);
    }

    @Transactional(readOnly = true)
    public CrewListDTO.ListResponse searchCrewsByName(String name) {
        List<Crew> crews = crewRepository.findByTitleContainingIgnoreCase(name);

        if (crews.isEmpty()) {
            throw new CrewSearchResultNotFoundException();
        }

        return convertToCrewListResponse(crews);
    }

    private CrewListDTO.ListResponse convertToCrewListResponse(List<Crew> crews) {
        List<CrewListDTO.Response> crewResponses = crews.stream()
                .map(crew -> {
                    List<CrewMember> activeMembers = crewMemberRepository.findByCrewId(crew.getId()).stream()
                            .filter(member -> member.getStatus() == CrewMemberStatus.ACTIVE)
                            .toList();
                    return CrewListDTO.Response.from(crew, activeMembers.size());
                })
                .toList();

        return CrewListDTO.ListResponse.of(crewResponses);
    }
    
    @Transactional(readOnly = true)
    public CrewDetailDTO.Response getCrewDetail(Long crewId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);
        
        List<CrewMember> allMembers = crewMemberRepository.findByCrewId(crewId);
        
        return CrewDetailDTO.Response.from(crew, allMembers);
    }
    
    @Transactional(readOnly = true)
    public CrewManagementDTO.PendingMembersResponse getPendingMembers(Long crewId, UserDetailsImpl userDetails) {
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);
        
        List<CrewMember> pendingMembers = crewMemberRepository.findByCrewId(crewId).stream()
                .filter(member -> member.getStatus() == CrewMemberStatus.PENDING)
                .toList();
        
        List<CrewManagementDTO.MemberInfo> memberInfos = pendingMembers.stream()
                .map(crewMember -> {
                    Member member = memberRepository.findById(crewMember.getMemberId())
                            .orElseThrow(MemberNotFoundException::new);
                    return CrewManagementDTO.MemberInfo.from(crewMember, member.getName(), member.getAge(), member.getGender());
                })
                .toList();
        
        return CrewManagementDTO.PendingMembersResponse.of(memberInfos);
    }
    
    @Transactional(readOnly = true)
    public CrewManagementDTO.BannedMembersResponse getBannedMembers(Long crewId, UserDetailsImpl userDetails) {
        crewRepository.findById(crewId)
                .orElseThrow(CrewNotFoundException::new);

        authorizationUtil.validateCrewManagementPermission(userDetails, crewId);
        
        List<CrewMember> bannedMembers = crewMemberRepository.findByCrewId(crewId).stream()
                .filter(member -> member.getStatus() == CrewMemberStatus.BANNED)
                .toList();
        
        List<CrewManagementDTO.MemberInfo> memberInfos = bannedMembers.stream()
                .map(crewMember -> {
                    Member member = memberRepository.findById(crewMember.getMemberId())
                            .orElseThrow(MemberNotFoundException::new);
                    return CrewManagementDTO.MemberInfo.from(crewMember, member.getName(), member.getAge(), member.getGender());
                })
                .toList();
        
        return CrewManagementDTO.BannedMembersResponse.of(memberInfos);
    }
    
    @Transactional(readOnly = true)
    public MemberProfileDTO getMemberProfile(Long targetMemberId, UserDetailsImpl userDetails) {
        memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(MemberNotFoundException::new);

        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(MemberNotFoundException::new);
        
        return MemberProfileDTO.from(targetMember);
    }
    
    private boolean isValidCrewRole(MemberRole role) {
        return role == MemberRole.CREW_MEMBER || role == MemberRole.CREW_MANAGER;
    }

    private List<CrewMember> getManagementMembers(Long crewId) {
        return crewMemberRepository.findByCrewId(crewId).stream()
                .filter(member -> member.getStatus() == CrewMemberStatus.ACTIVE)
                .filter(member -> member.getRole() == MemberRole.CREW_LEADER ||
                                member.getRole() == MemberRole.CREW_MANAGER)
                .toList();
    }
}