package org.nowcat.nowcat.domain.admin.auth.service;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.admin.auth.dto.AdminAuthLoginRequestDto;
import org.nowcat.nowcat.domain.admin.auth.dto.AdminAuthTokenResponseDto;
import org.nowcat.nowcat.domain.admin.auth.entity.AdminLoginAttempt;
import org.nowcat.nowcat.domain.admin.auth.repository.AdminLoginAttemptRepository;
import org.nowcat.nowcat.global.auth.jwt.JwtProvider;
import org.nowcat.nowcat.global.exception.CustomException;
import org.nowcat.nowcat.global.exception.constants.ExceptionCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    @Value("${admin.id}")
    private String ADMIN_ID;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    @Value("${admin.login.max.attempts}")
    private int MAX_ATTEMPTS;

    private final PasswordEncoder passwordEncoder;
    private final AdminLoginAttemptRepository adminLoginAttemptRepository;
    private final JwtProvider jwtProvider;

    @Transactional(noRollbackFor = CustomException.class) //로그인 실패시 시도 횟수 저장을 위함
    public AdminAuthTokenResponseDto login(final AdminAuthLoginRequestDto loginRequestDto) {

        // 1) null 검사
        if (
                loginRequestDto.getId() == null || loginRequestDto.getId().isEmpty()
                ||  loginRequestDto.getPassword() == null || loginRequestDto.getPassword().isEmpty()
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 로그인 실패 횟수 조회 (없으면 새로 저장)
        final AdminLoginAttempt adminLoginAttempt = adminLoginAttemptRepository.findById(1L)
                .orElseGet(() -> adminLoginAttemptRepository.save(
                        AdminLoginAttempt.builder()
                                .initialAttempts(0)
                                .build()
                ));

        // 3) 24시간 지났으면 초기화
        if (adminLoginAttempt.getUpdatedAt().plusDays(1).isBefore(LocalDateTime.now())) {
            adminLoginAttempt.updateAttempt(0);
        }

        // 4) 실패 횟수 확인
        if (adminLoginAttempt.getAttempts() >= MAX_ATTEMPTS) {
            throw new CustomException(ExceptionCode.TOO_MANY_LOGIN_ATTEMPTS);
        }

        // 5) 비교
        if (
                !isEqual(loginRequestDto.getId(), ADMIN_ID)
                || !passwordEncoder.matches(loginRequestDto.getPassword(), ADMIN_PASSWORD)
        ) {
            //실패 횟수 증가 (noRollback 때문에 저장됨)
            adminLoginAttempt.addAttempt();
            throw new CustomException(ExceptionCode.LOGIN_FAILED);
        }

        // 6) JWT 발급
        final String accessToken = jwtProvider.createAccessToken();
        final String refreshToken = jwtProvider.createRefreshToken();

        return AdminAuthTokenResponseDto.builder()
                .accessToken(accessToken)
                .tokenType(jwtProvider.getTokenType())
                .exprTime(jwtProvider.getAccessTokenExpirationSeconds())
                .refreshToken(refreshToken)
                .build();

    }

    /**
     * 문자열 비교시 비교 시간을 일정하게 하기 위한 메서드
     * @param strA 첫번째 문자열
     * @param strB 두번째 문자열
     * @return 같은지 여부
     */
    private boolean isEqual(
            final String strA,
            final String strB
    ) {

        if(strA == null || strB == null) {
            return false;
        }

        return MessageDigest.isEqual(
                strA.getBytes(StandardCharsets.UTF_8),
                strB.getBytes(StandardCharsets.UTF_8)
        );
    }
}
