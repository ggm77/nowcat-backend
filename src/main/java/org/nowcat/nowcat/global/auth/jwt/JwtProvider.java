package org.nowcat.nowcat.global.auth.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.accessToken.exprTime}")
    private Integer ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refreshToken.exprTime}")
    private Integer REFRESH_TOKEN_EXPIRATION_TIME;

    /**
     * 액세스 토큰 생성하는 메서드
     * 관리자만 JWT 사용하기 때문에 sub에는 특별한 값 X
     * @return 액세스 토큰
     */
    public String createAccessToken() {

        final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

        final String sub = "ADMIN";
        final Instant now = Instant.now();
        final Instant exp = now.plusSeconds(ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(sub)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /**
     * 리프레시 토큰 생성하는 메서드
     * 관리자만 JWT 사용하기 때문에 sub에는 특별한 값 X
     * @return 리프레시 토큰
     */
    public String createRefreshToken() {

        final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

        final String sub = "ADMIN";
        final Instant now = Instant.now();
        final Instant exp = now.plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(sub)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String getTokenType() {
        return "Bearer";
    }

    public Long getAccessTokenExpirationSeconds() {
        return Long.valueOf(ACCESS_TOKEN_EXPIRATION_TIME);
    }

    /**
     * JWT가 유효한지 검사하는 메서드
     * @param token JWT
     * @return 유효한지 여부
     */
    public boolean validateToken(final String token) {

        final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (final JwtException ex) {
            return false;
        }
    }
}
