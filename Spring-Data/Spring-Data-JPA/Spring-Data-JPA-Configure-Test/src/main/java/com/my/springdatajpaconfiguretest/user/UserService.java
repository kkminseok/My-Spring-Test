package com.my.springdatajpaconfiguretest.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllByName(String name) {
        return userRepository.findAllByName(name);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }
}
