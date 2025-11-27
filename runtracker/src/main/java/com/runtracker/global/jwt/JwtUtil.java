package com.runtracker.global.jwt;

import com.runtracker.global.jwt.dto.TokenDataDto;
import com.runtracker.global.jwt.exception.ExpiredJwtTokenException;
import com.runtracker.global.jwt.exception.InvalidJwtTokenException;
import com.runtracker.global.jwt.exception.JwtClaimsEmptyException;
import com.runtracker.global.jwt.exception.UnsupportedJwtTokenException;
import com.runtracker.global.jwt.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                   @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
                   TokenBlacklistService tokenBlacklistService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String generateAccessToken(Long memberId, String socialId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        String token = Jwts.builder()
                .setSubject(memberId.toString())
                .claim("socialId", socialId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        tokenBlacklistService.trackUserToken(memberId, token, 
            java.time.Duration.ofMillis(accessTokenExpiration));
        
        return token;
    }

    public String generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        String token = Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        tokenBlacklistService.trackUserToken(memberId, token, 
            java.time.Duration.ofMillis(refreshTokenExpiration));
        
        return token;
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            throw new ExpiredJwtTokenException();
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            throw new InvalidJwtTokenException();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new UnsupportedJwtTokenException();
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new JwtClaimsEmptyException();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidJwtTokenException();
        }
    }

    public void validateToken(String token) {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("Token is blacklisted");
                throw new InvalidJwtTokenException();
            }
            
            parseToken(token);
        } catch (ExpiredJwtTokenException | UnsupportedJwtTokenException | 
                 JwtClaimsEmptyException | InvalidJwtTokenException e) {
            throw e;
        }
    }

    public void blacklistToken(String token) {
        try {
            Claims claims = parseToken(token);
            long expirationTime = claims.getExpiration().getTime();
            tokenBlacklistService.blacklistToken(token, expirationTime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    public Long getMemberIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getSocialIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("socialId", String.class);
    }

    public TokenDataDto createTokenData(Long memberId, String socialId) {
        Date accessTokenExpiry = new Date(System.currentTimeMillis() + accessTokenExpiration);
        Date refreshTokenExpiry = new Date(System.currentTimeMillis() + refreshTokenExpiration);

        String accessToken = generateAccessToken(memberId, socialId);
        String refreshToken = generateRefreshToken(memberId);

        return TokenDataDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(accessTokenExpiry.getTime())
                .refreshTokenExpiredAt(refreshTokenExpiry.getTime())
                .build();
    }

    public TokenDataDto refreshToken(String refreshToken) {
        validateToken(refreshToken);
        
        Claims claims = parseToken(refreshToken);
        Long memberId = Long.valueOf(claims.getSubject());
        String socialId = claims.get("socialId", String.class);

        return createTokenData(memberId, socialId);
    }
}