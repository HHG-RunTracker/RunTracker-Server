package com.runtracker.domain.crew.service;

import com.runtracker.domain.crew.dto.CrewCreateDTO;
import com.runtracker.domain.crew.entity.Crew;
import com.runtracker.domain.crew.entity.CrewMember;
import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.exception.CrewAlreadyExistsException;
import com.runtracker.domain.crew.exception.MemberNotFoundException;
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
}