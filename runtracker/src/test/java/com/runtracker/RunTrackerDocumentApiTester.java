package com.runtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runtracker.domain.member.service.MemberService;
import com.runtracker.domain.member.service.AuthService;
import com.runtracker.global.jwt.JwtAuthenticationFilter;
import com.runtracker.global.jwt.JwtUtil;
import com.runtracker.global.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class RunTrackerDocumentApiTester {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtUtil jwtUtil;

    @MockitoBean
    protected MemberService memberService;
    
    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserDetailsServiceImpl userDetailsService;

    protected final static String AUTH_HEADER = "Authorization";
    protected final static String TEST_ACCESS_TOKEN = "Bearer testAccessToken";

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(new JwtAuthenticationFilter(jwtUtil, userDetailsService, List.of("/swagger-ui/**", "/api-docs/**")))
                .build();

        SecurityContext securityContext = SecurityContextHolder.getContext();

        // Mock Principal 생성 (실제 UserDetails 없이 간단하게)
        var principal = mock(Object.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, new ArrayList<>()
        );
        securityContext.setAuthentication(authentication);
    }

    /**
     * 인증이 필요한 API 테스트를 위한 헬퍼 메서드
     */
    protected Authentication createMockAuthentication(Long memberId, String socialId) {
        return new UsernamePasswordAuthenticationToken(
                memberId, socialId, new ArrayList<>()
        );
    }

    /**
     * JSON 변환 헬퍼 메서드
     */
    protected String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}