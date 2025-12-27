package com.my.hellosecurity.config;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 인증 논리를 추가해야함
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if ("minseok".equals(username) && "12345".equals(password)) {
            //인증 성공시 Authentication 객체를 반환해야함
            return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList());
        } else {
            throw new AuthenticationCredentialsNotFoundException("인증 실패");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //Authentication 형식의 구현을 추가해야함. 5장에서 진행

        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
