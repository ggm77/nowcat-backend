package org.nowcat.nowcat.global.config;

import lombok.RequiredArgsConstructor;
import org.nowcat.nowcat.global.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                //CORS 설정
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

                //JWT 사용을 위해서 기존 보안 장치 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //인증 필요로 하는 엔드포인트 설정
                .authorizeHttpRequests(auth -> auth
                        //어드민 API 중 인증 관련은 인증 필요 X
                        .requestMatchers("/api/v1/admin/auth/**").permitAll()
                        //나머지 어드민 API만 인증 필요
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .anyRequest().permitAll()
                );

        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 기본 사용자 생성을 막기 위해 빈 유저 서비스 등록
        return new InMemoryUserDetailsManager();
    }
}
