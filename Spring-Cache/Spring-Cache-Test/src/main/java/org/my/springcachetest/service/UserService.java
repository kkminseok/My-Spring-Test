package org.my.springcachetest.service;

import org.my.springcachetest.domain.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 👤 Spring Cache 학습용 UserService - @CacheConfig 활용
 * =============================================================================
 *
 * @CacheConfig를 사용하여 클래스 레벨에서 공통 캐시 설정을 지정합니다.
 * 개별 메서드에서 cacheNames를 생략할 수 있습니다.
 */
@Service
@CacheConfig(cacheNames = "users")  // 💡 클래스의 모든 캐시 메서드에 적용될 기본 캐시 이름
public class UserService {

    private final Map<Long, User> userStore = new HashMap<>();
    private final AtomicInteger findByIdCallCount = new AtomicInteger(0);
    private final AtomicInteger updateUserCallCount = new AtomicInteger(0);

    public UserService() {
        userStore.put(1L, new User(1L, "alice", "alice@example.com", 25));
        userStore.put(2L, new User(2L, "bob", "bob@example.com", 30));
        userStore.put(3L, new User(3L, "charlie", "charlie@example.com", 35, false)); // 비활성 사용자
    }

    // =========================================================================
    // 📌 @CacheConfig 덕분에 cacheNames 생략 가능
    // =========================================================================
    /**
     * @CacheConfig에서 지정한 "users" 캐시 사용
     *
     * 💡 학습 포인트:
     * - cacheNames를 생략해도 @CacheConfig의 설정이 적용됨
     */
    @Cacheable
    public User findById(Long id) {
        findByIdCallCount.incrementAndGet();
        simulateSlowService();
        return userStore.get(id);
    }

    // =========================================================================
    // 📌 SpEL - 파라미터 인덱스로 접근
    // =========================================================================
    /**
     * #p0, #a0, #root.args[0] 모두 첫 번째 파라미터를 의미
     *
     * 💡 학습 포인트:
     * - #p0: 파라미터 인덱스 (p = parameter)
     * - #a0: 파라미터 인덱스 (a = argument)
     * - #root.args[0]: root 객체를 통한 접근
     */
    @Cacheable(key = "#p0")  // #id와 동일
    public User findByIdUsingIndex(Long id) {
        findByIdCallCount.incrementAndGet();
        simulateSlowService();
        return userStore.get(id);
    }

    // =========================================================================
    // 📌 SpEL - 객체 속성 접근
    // =========================================================================
    /**
     * 파라미터 객체의 속성을 키로 사용
     *
     * 💡 학습 포인트:
     * - #user.id: User 객체의 id 속성
     * - 중첩 속성도 가능: #user.address.city 등
     */
    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        updateUserCallCount.incrementAndGet();
        userStore.put(user.getId(), user);
        return user;
    }

    // =========================================================================
    // 📌 SpEL - root 객체 활용
    // =========================================================================
    /**
     * #root 객체를 통해 메서드 정보에 접근
     *
     * 💡 학습 포인트:
     * - #root.methodName: 메서드 이름
     * - #root.method.name: 메서드 이름
     * - #root.targetClass: 대상 클래스
     * - #root.caches[0].name: 첫 번째 캐시 이름
     */
    @Cacheable(key = "#root.methodName + '_' + #id")
    public User findByIdWithMethodName(Long id) {
        findByIdCallCount.incrementAndGet();
        simulateSlowService();
        return userStore.get(id);
    }

    // =========================================================================
    // 📌 SpEL - 복합 조건
    // =========================================================================
    /**
     * 여러 조건을 조합한 캐싱
     *
     * 💡 학습 포인트:
     * - condition: AND, OR 등 논리 연산 가능
     * - unless: 결과값 기반 필터링
     */
    @Cacheable(
            cacheNames = "activeUsers",
            condition = "#id != null && #id > 0",
            unless = "#result == null || !#result.active"
    )
    public User findActiveUser(Long id) {
        findByIdCallCount.incrementAndGet();
        simulateSlowService();
        return userStore.get(id);
    }

    // =========================================================================
    // 📌 개별 메서드에서 @CacheConfig 설정 오버라이드
    // =========================================================================
    /**
     * 클래스 레벨 설정을 메서드에서 오버라이드
     *
     * 💡 학습 포인트:
     * - @CacheConfig의 cacheNames="users"를 "premiumUsers"로 오버라이드
     */
    @Cacheable(cacheNames = "premiumUsers")  // @CacheConfig 설정 오버라이드
    public User findPremiumUser(Long id) {
        findByIdCallCount.incrementAndGet();
        simulateSlowService();
        return userStore.get(id);
    }

    @CacheEvict
    public void deleteUser(Long id) {
        userStore.remove(id);
    }

    @CacheEvict(allEntries = true)
    public void clearAllUsers() {
        userStore.clear();
    }

    // =========================================================================
    // 유틸리티 메서드
    // =========================================================================
    private void simulateSlowService() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFindByIdCallCount() {
        return findByIdCallCount.get();
    }

    public int getUpdateUserCallCount() {
        return updateUserCallCount.get();
    }

    public void resetCallCounts() {
        findByIdCallCount.set(0);
        updateUserCallCount.set(0);
    }

    public void resetUserStore() {
        userStore.clear();
        userStore.put(1L, new User(1L, "alice", "alice@example.com", 25));
        userStore.put(2L, new User(2L, "bob", "bob@example.com", 30));
        userStore.put(3L, new User(3L, "charlie", "charlie@example.com", 35, false));
    }

    public User getUserDirectly(Long id) {
        return userStore.get(id);
    }

    public void addUser(User user) {
        userStore.put(user.getId(), user);
    }
}
