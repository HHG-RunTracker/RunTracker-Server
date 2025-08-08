package com.runtracker.global.security;

import com.runtracker.domain.member.entity.Member;
import com.runtracker.domain.member.entity.enums.MemberRole;
import com.runtracker.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to load user by memberId: " + memberId));
        
        return createUserDetails(member);
    }

    private UserDetails createUserDetails(Member member) {
        // TODO: 나중에 Member 엔티티에 roles 필드가 추가되면 실제 권한 사용
        // 현재는 기본적으로 USER 권한만 부여
        List<MemberRole> roles = List.of(MemberRole.USER);
        
        return UserDetailsImpl.builder()
                .memberId(member.getId())
                .socialId(member.getSocialId())
                .roles(roles)
                .build();
    }
}