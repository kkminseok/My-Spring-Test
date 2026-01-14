package org.my.springcachetest.domain;

import java.util.Objects;

/**
 * 사용자 도메인 모델
 * SpEL 표현식 테스트용 엔티티
 */
public class User {

    private Long id;
    private String username;
    private String email;
    private int age;
    private boolean active;

    public User() {}

    public User(Long id, String username, String email, int age) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.active = true;
    }

    public User(Long id, String username, String email, int age, boolean active) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email +
               "', age=" + age + ", active=" + active + "}";
    }
}
