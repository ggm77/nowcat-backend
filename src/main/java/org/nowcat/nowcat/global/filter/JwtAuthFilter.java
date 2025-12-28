package org.nowcat.nowcat.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.global.auth.jwt.JwtProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        //어드민 API들만 필터, 나머지는 그냥 통과
        return !request.getServletPath().startsWith("/api/v1/admin");
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) Authorization 헤더에서 값 추출
        final String authorizationHeaderValue = request.getHeader("Authorization");

        // 2) 값 존재 확인 (없으면 익명 진행)
        if(authorizationHeaderValue == null || !authorizationHeaderValue.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) JWT만 파싱
        final String token = authorizationHeaderValue.substring(7);

        // 4) JWT 유효성 검사 (유효하지 않으면 익명 진행)
        if(!jwtProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5) 인증 객체 설정
        //관리자만 JWT를 사용하기 때문에 하드코딩으로 정보 설정
        final List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        final Authentication auth =
                new UsernamePasswordAuthenticationToken("ADMIN", null, authorities);

        // 6) 보안 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
