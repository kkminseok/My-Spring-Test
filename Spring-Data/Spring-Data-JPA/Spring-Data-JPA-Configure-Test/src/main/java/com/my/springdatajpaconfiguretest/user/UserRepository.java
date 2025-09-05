package com.my.springdatajpaconfiguretest.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface UserRepository extends CrudRepository<User,Long> {

    List<User> findAllByName(String name);
    User save(User user);

}
