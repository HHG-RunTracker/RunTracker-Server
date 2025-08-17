package com.runtracker.global.security;

import com.runtracker.domain.crew.enums.CrewMemberStatus;
import com.runtracker.domain.crew.repository.CrewMemberRepository;
import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.member.repository.MemberRepository;
import com.runtracker.global.security.dto.CrewMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to load user by memberId: " + memberId));
        
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        List<MemberRole> roles = new ArrayList<>();
        roles.add(MemberRole.USER);
        
        // 활성 크루 멤버십 조회 (한 명당 크루 1개만 가능)
        CrewMembership crewMembership = crewMemberRepository
                .findByMemberIdAndStatus(member.getId(), CrewMemberStatus.ACTIVE)
                .stream()
                .findFirst()
                .map(crewMember -> CrewMembership.builder()
                        .crewId(crewMember.getCrewId())
                        .role(crewMember.getRole())
                        .build())
                .orElse(null);
        
        return UserDetailsImpl.builder()
                .memberId(member.getId())
                .socialId(member.getSocialId())
                .roles(roles)
                .crewMembership(crewMembership)
                .build();
    }
}