package org.nowcat.nowcat.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Value("${admin.custom-header.name}")
    private String ADMIN_CUSTOM_HEADER_NAME;

    @Value("${admin.secret-key}")
    private String ADMIN_SECRET_KEY;

    @Override
    public boolean preHandle(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final Object handler
    ) throws Exception {

        //클라이언트가 보낸 key 가져오기
        final String clientKey = httpServletRequest.getHeader(ADMIN_CUSTOM_HEADER_NAME);

        //키가 같은지 확인
        if (clientKey == null || !isEqualConstantTime(clientKey, ADMIN_SECRET_KEY)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.getWriter().write("Admin Access Only");
            return false;
        }

        return true;
    }

    /**
     * 글자 비교시 동일한 시간 걸리게 하는 메서드
     * @param strA 첫번째 문자열
     * @param strB 두번째 문자열
     * @return 같은지 여부
     */
    private boolean isEqualConstantTime(
            final String strA,
            final String strB
    ) {
        return MessageDigest.isEqual(
                strA.getBytes(StandardCharsets.UTF_8),
                strB.getBytes(StandardCharsets.UTF_8)
        );
    }
}
