package org.my.springcachetest;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.User;
import org.my.springcachetest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * ⚙️ @CacheConfig 학습 테스트 - 클래스 레벨 캐시 설정
 * =============================================================================
 *
 * @CacheConfig는 클래스의 모든 캐시 메서드에 공통 설정을 적용합니다.
 *
 * 설정 가능한 속성:
 * - cacheNames: 기본 캐시 이름
 * - keyGenerator: 커스텀 키 생성기
 * - cacheManager: 특정 캐시 매니저
 * - cacheResolver: 커스텀 캐시 리졸버
 *
 * 💡 장점:
 * - 반복되는 설정을 한 곳에서 관리
 * - 개별 메서드에서 필요시 오버라이드 가능
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheConfigTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        userService.resetCallCounts();
        userService.resetUserStore();
    }

    // =========================================================================
    // 📌 테스트 1: @CacheConfig 기본 동작
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("@CacheConfig: 클래스 레벨에서 지정한 캐시 이름 사용")
    void test_CacheConfig_DefaultCacheName() {
        // Given: UserService는 @CacheConfig(cacheNames = "users")가 적용됨
        // findById()는 @Cacheable만 있고 cacheNames 생략

        // When: 유저 조회
        userService.findById(1L);

        // Then: "users" 캐시에 저장됨
        Cache usersCache = cacheManager.getCache("users");

        // TODO: 빈칸을 채우세요
        // @CacheConfig에서 지정한 "users" 캐시에 저장되었는지 확인
        boolean existsInUsersCache = (usersCache != null && usersCache.get(1L) != null);
        assertEquals(true, existsInUsersCache,
                "@CacheConfig의 기본 캐시 이름이 적용되었나요?");
    }

    // =========================================================================
    // 📌 테스트 2: 메서드에서 @CacheConfig 오버라이드
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("@CacheConfig 오버라이드: 메서드에서 다른 캐시 이름 지정")
    void test_CacheConfig_Override() {
        // Given: findPremiumUser()는 @Cacheable(cacheNames = "premiumUsers")로 오버라이드

        // When: 프리미엄 유저 조회
        userService.findPremiumUser(1L);

        // Then: "premiumUsers" 캐시에 저장됨 (users 아님)
        Cache premiumUsersCache = cacheManager.getCache("premiumUsers");
        Cache usersCache = cacheManager.getCache("users");

        boolean existsInPremiumCache = (premiumUsersCache != null && premiumUsersCache.get(1L) != null);
        boolean existsInUsersCache = (usersCache != null && usersCache.get(1L) != null);

        // TODO: 빈칸을 채우세요
        assertEquals(true, existsInPremiumCache, "premiumUsers 캐시에 저장되었나요?");
        assertEquals(false, existsInUsersCache, "users 캐시에는 저장되지 않았나요?");
    }

    // =========================================================================
    // 📌 테스트 3: @CacheConfig와 함께 동작하는 다양한 어노테이션
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("@CacheConfig: @Cacheable, @CachePut, @CacheEvict 모두 적용")
    void test_CacheConfig_AllAnnotations() {
        // @Cacheable: 캐시에 저장
        userService.findById(1L);

        // @CachePut: 캐시 업데이트
        User updatedUser = new User(1L, "alice_updated", "alice_new@example.com", 26);
        userService.updateUser(updatedUser);

        // @CacheEvict: 캐시에서 삭제
        userService.deleteUser(1L);

        // Then: 모두 "users" 캐시에 대해 동작
        Cache usersCache = cacheManager.getCache("users");
        boolean deletedFromCache = (usersCache.get(1L) == null);

        // TODO: 빈칸을 채우세요
        assertEquals(true, deletedFromCache,
                "@CacheEvict가 @CacheConfig의 캐시 이름을 사용했나요?");
    }

    // =========================================================================
    // 📌 테스트 4: @CacheConfig가 없는 경우와 비교
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("@CacheConfig 장점: 코드 중복 감소")
    void test_CacheConfig_ReducesDuplication() {
        /*
         * @CacheConfig가 없다면 매 메서드마다 cacheNames를 지정해야 함:
         *
         * @Cacheable(cacheNames = "users")
         * public User findById(Long id) {...}
         *
         * @CachePut(cacheNames = "users", key = "#user.id")
         * public User updateUser(User user) {...}
         *
         * @CacheEvict(cacheNames = "users")
         * public void deleteUser(Long id) {...}
         *
         * @CacheConfig(cacheNames = "users")를 사용하면:
         *
         * @Cacheable  // cacheNames 생략 가능
         * public User findById(Long id) {...}
         *
         * @CachePut(key = "#user.id")  // cacheNames 생략 가능
         * public User updateUser(User user) {...}
         *
         * @CacheEvict  // cacheNames 생략 가능
         * public void deleteUser(Long id) {...}
         */

        // 실제로 모든 메서드가 같은 캐시를 사용하는지 확인
        userService.findById(2L);

        Cache usersCache = cacheManager.getCache("users");
        boolean cached = (usersCache.get(2L) != null);

        // TODO: 빈칸을 채우세요
        assertEquals(true, cached, "@CacheConfig 덕분에 cacheNames 생략해도 동작");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: true
     * 테스트 2: true, false
     * 테스트 3: true
     * 테스트 4: true
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final boolean _____ = true;  // true 또는 false로 바꾸세요
}
