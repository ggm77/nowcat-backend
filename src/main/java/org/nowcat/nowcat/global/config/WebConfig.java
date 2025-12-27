package org.nowcat.nowcat.global.config;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.global.interceptor.AdminAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String[] ALLOWED_ORIGINS;

    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry){

        //관리자 API에 관리자 인증 인터셉터 추가
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/v1/admin/**");
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ALLOWED_ORIGINS)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
