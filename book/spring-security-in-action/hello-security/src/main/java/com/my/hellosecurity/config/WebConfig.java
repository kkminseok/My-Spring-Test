package com.my.hellosecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class WebConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.authorizeHttpRequests((auth) -> auth
                        .anyRequest().authenticated()) // 전체 요청에 대한 허용을 어떻게 할 것인가? 모두 허용, 모두 접근제한
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(new CustomAuthenticationProvider());
        return httpSecurity.build();
    }
}
