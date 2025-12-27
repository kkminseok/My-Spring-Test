package com.my.hellosecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectConfig {

    // AuthenticationProvider를 사용하므로 UserDetailsService 주석처리
//    @Bean
//    public UserDetailsService userDetailsService(AuthenticationManagerBuilder auth) {
//        // 보안을위해 변수형식을 감춤
//        var inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        var user = User.withUsername("minseok")
//                .password("{noop}12345")
//                .authorities("read")
//                .build();
//        inMemoryUserDetailsManager.createUser(user);
//        auth.userDetailsService(inMemoryUserDetailsManager)
//                .passwordEncoder(passwordEncoder());
//        return inMemoryUserDetailsManager;
//    }

    // AuthenticationProvider를 사용하므로 PasswordEncoder 주석처리
    /**
     * NoOpPasswordEncoder는 암호를 평문으로 저장 및 deprecated되어 최신식으로 변경
     * @return
     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
}
