package com.phofor.phocaforme.auth.config.security;

import com.phofor.phocaforme.auth.config.handler.CustomAuthenticationEntryPoint;
import com.phofor.phocaforme.auth.config.handler.CustomLogoutHandler;
import com.phofor.phocaforme.auth.config.handler.OAuthLoginFailureHandler;
import com.phofor.phocaforme.auth.config.handler.OAuthLoginSuccessHandler;
import com.phofor.phocaforme.auth.filter.KakaoAuthenticationTokenFilter;
import com.phofor.phocaforme.auth.service.redis.RedisService;
import com.phofor.phocaforme.auth.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

/**
 * Spring Security 설정
 * Logout시 Handler 설정(Spring Chain Filter 순서상 2번 이므로 앞쪽으로 설정함)
 * 에러 핸들러
 * CORS 설정, CSRF 설정 생략
 * OAuth 인증을 사용하여 일반 로그인 기능 끔
 * 로그인 상태를 토큰으로 관리하기 때문에 무상태 유지(세션으로 관리x)
 * 사용자 정의 필터를 정의하여 토큰이 있는 경우 로그인 인증 절차 넘김
 * URL에 대한 인증처리
 * Oauth2.0 로그인 설정
 * 로그인 성공시 처리 로직
 * 로그인 실패시 처리 로직
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final UserService userService;
    private final RedisService redisService;

    @Bean
    public CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler(redisService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .deleteCookies("JSESSIONID", "token") // 쿠키 삭제
                        .invalidateHttpSession(true) // 세션 무효화
                        .addLogoutHandler(customLogoutHandler())
                )
                .exceptionHandling(error -> error
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(CsrfConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .rememberMe(RememberMeConfigurer::disable)
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterAfter(new KakaoAuthenticationTokenFilter(redisService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers("/auth/**", "/main", "/error",
                                "firebase/**", "/css/**","/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                // oauth2.0 로그인 설정
                .oauth2Login(oAuth -> oAuth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(userService)
                        )
                        .successHandler(oAuthLoginSuccessHandler)
                        .failureHandler(oAuthLoginFailureHandler)
                );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:3000"));
            config.setAllowCredentials(true);
            return config;
        };
    }

}
