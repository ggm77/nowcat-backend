package org.nowcat.nowcat.domain.admin.auth.controller;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.domain.admin.auth.dto.AdminAuthLoginRequestDto;
import org.nowcat.nowcat.domain.admin.auth.dto.AdminAuthTokenResponseDto;
import org.nowcat.nowcat.domain.admin.auth.service.AdminAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<AdminAuthTokenResponseDto> login(
            @RequestBody final AdminAuthLoginRequestDto loginRequestDto
    ) {

        return ResponseEntity.ok().body(adminAuthService.login(loginRequestDto));
    }
}
