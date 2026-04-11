package com.sm_four_idiot.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

/**
 * Spring Security 설정 클래스
 * - JWT 기반 Stateless 인증 방식 사용 (세션 미사용)
 * - 권한별 API 접근 제어
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 비밀번호 암호화 방식 설정 (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 필터 체인 설정
     * - CSRF 비활성화 (JWT 사용으로 불필요)
     * - 세션 미사용 (Stateless)
     * - API별 권한 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 미사용 (JWT Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // API별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 API
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()
                        // 단어 조회는 GET만 허용
                        .requestMatchers(HttpMethod.GET, "/api/words", "/api/words/**").permitAll()
                        // 테스트 문제 출제는 GET 허용
                        .requestMatchers(HttpMethod.GET, "/api/test/questions").permitAll()
                        // 관리자만 접근 가능한 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 로그인 필요
                        .anyRequest().authenticated()
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}