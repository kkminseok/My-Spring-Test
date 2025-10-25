package com.my.springboot4demo.RestTest.service;


import com.my.springboot4demo.RestTest.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public UserDto findById(Long id) {
        // 여기선 실제 DB 조회 대신 하드코딩 또는 모킹 가능
        return new UserDto(id, "user" + id, "user" + id + "@example.com");
    }
}
