package org.my.springcachetest;

import org.junit.jupiter.api.*;
import org.my.springcachetest.service.SelfInvocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * ⚠️ Self-Invocation (자기 호출) 문제 학습 테스트
 * =============================================================================
 *
 * Spring AOP 프록시 모드의 중요한 제한사항을 학습합니다.
 *
 * 💡 핵심 개념:
 * Spring Cache는 프록시 패턴을 사용합니다.
 *
 * 외부에서 호출할 때:
 * Client -> Proxy(캐시 로직) -> Target(실제 메서드)
 *           ↑ 캐시 체크/저장
 *
 * 내부에서 호출할 때 (Self-Invocation):
 * Target -> Target (프록시를 거치지 않음!)
 *           ↑ 캐시 로직이 실행되지 않음
 *
 * 🚨 결과:
 * 같은 클래스 내에서 @Cacheable 메서드를 호출하면 캐시가 동작하지 않습니다!
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SelfInvocationTest {

    @Autowired
    private SelfInvocationService selfInvocationService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        selfInvocationService.resetCallCounts();
    }

    // =========================================================================
    // 📌 테스트 1: 외부 호출 - 캐시 정상 동작
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("외부 호출: 프록시를 통해 캐시 정상 동작")
    void test_ExternalCall_CacheWorks() {
        // Given: 외부(테스트 클래스)에서 직접 호출

        // When: 같은 키로 두 번 호출
        selfInvocationService.getCachedData("key1");
        selfInvocationService.getCachedData("key1");

        // Then: 프록시를 통한 호출이므로 캐시 동작
        // 첫 번째: 캐시 미스 -> 메서드 실행 -> 캐시 저장
        // 두 번째: 캐시 히트 -> 메서드 실행 X

        // TODO: 빈칸을 채우세요
        assertEquals(_____, selfInvocationService.getCachedMethodCallCount(),
                "외부 호출 시 캐시가 동작하여 메서드는 1번만 실행됩니다");
    }

    // =========================================================================
    // 📌 테스트 2: Self-Invocation - 캐시 동작 안함! ⚠️
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("⚠️ Self-Invocation: 같은 클래스 내 호출은 캐시가 동작하지 않음")
    void test_SelfInvocation_CacheNotWorks() {
        // Given: wrapperMethod()가 내부적으로 getCachedData()를 호출
        // wrapperMethod() -> getCachedData() (같은 클래스)

        // When: wrapperMethod를 두 번 호출
        selfInvocationService.wrapperMethod("key1");
        selfInvocationService.wrapperMethod("key1");

        // Then: 🚨 Self-Invocation으로 인해 캐시가 동작하지 않음!
        // wrapperMethod 내부에서 getCachedData()를 호출할 때 프록시를 거치지 않음
        // 따라서 getCachedData()가 매번 실행됨

        // TODO: 빈칸을 채우세요
        assertEquals(_____, selfInvocationService.getCachedMethodCallCount(),
                "Self-Invocation: 캐시가 동작하지 않아 메서드가 매번 실행됩니다");
    }

    // =========================================================================
    // 📌 테스트 3: 외부 호출 vs Self-Invocation 비교
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("비교: 외부 호출 vs Self-Invocation")
    void test_Comparison() {
        String key = "testKey";

        // ===== 외부 호출 =====
        selfInvocationService.getCachedData(key);
        selfInvocationService.getCachedData(key);
        int externalCallCount = selfInvocationService.getCachedMethodCallCount();

        selfInvocationService.resetCallCounts();
        // 캐시 클리어 - 공정한 비교를 위해
        cacheManager.getCache("selfInvocationTest").clear();

        // ===== Self-Invocation =====
        selfInvocationService.wrapperMethod(key);
        selfInvocationService.wrapperMethod(key);
        int selfInvocationCallCount = selfInvocationService.getCachedMethodCallCount();

        // Then: 극명한 차이!
        // - 외부 호출: 캐시 동작 -> 1번 실행
        // - Self-Invocation: 캐시 안됨 -> 2번 실행

        // TODO: 빈칸을 채우세요
        assertEquals(_____, externalCallCount, "외부 호출: 캐시 동작");
        assertEquals(_____, selfInvocationCallCount, "Self-Invocation: 캐시 미동작");

        // 이 차이가 Self-Invocation 문제입니다!
        assertNotEquals(externalCallCount, selfInvocationCallCount,
                "같은 메서드인데 호출 방식에 따라 결과가 다릅니다!");
    }

    // =========================================================================
    // 📌 테스트 4: 혼합 시나리오
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("혼합: 외부 호출 후 Self-Invocation")
    void test_MixedScenario() {
        String key = "mixedKey";

        // Step 1: 외부에서 직접 호출 -> 캐시에 저장됨
        selfInvocationService.getCachedData(key);
        assertEquals(1, selfInvocationService.getCachedMethodCallCount());

        // Step 2: Self-Invocation으로 호출
        // wrapperMethod -> getCachedData (내부 호출)
        selfInvocationService.wrapperMethod(key);

        // Then: Self-Invocation은 캐시를 체크하지 않으므로 메서드 실행됨
        // TODO: 빈칸을 채우세요
        assertEquals(_____, selfInvocationService.getCachedMethodCallCount(),
                "이미 캐시에 있어도 Self-Invocation은 캐시를 사용하지 않습니다");
    }

    // =========================================================================
    // 📌 테스트 5: 해결 방법 이해하기
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("💡 Self-Invocation 해결 방법 이해")
    void test_UnderstandingSolutions() {
        /*
         * 🔧 Self-Invocation 문제 해결 방법:
         *
         * 1. ✅ 별도의 Bean으로 분리 (권장)
         *    - 캐시 메서드를 다른 서비스 클래스로 이동
         *    - 가장 깔끔하고 테스트하기 쉬운 방법
         *
         *    예시:
         *    @Service
         *    class DataService {
         *        @Autowired
         *        private CacheService cacheService;  // 별도 빈
         *
         *        public String getData(String key) {
         *            return cacheService.getCachedData(key);  // 외부 호출!
         *        }
         *    }
         *
         *    @Service
         *    class CacheService {
         *        @Cacheable("data")
         *        public String getCachedData(String key) {
         *            return expensiveOperation(key);
         *        }
         *    }
         *
         * 2. ⚠️ AspectJ 모드 사용
         *    - @EnableCaching(mode = AdviceMode.ASPECTJ)
         *    - 컴파일 타임 위빙 또는 로드 타임 위빙 필요
         *    - 설정이 복잡하지만 프록시 제한 해결
         *
         * 3. ❌ Self-injection (권장하지 않음)
         *    - @Lazy를 사용해 자기 자신을 주입
         *    - 순환 의존성 문제 및 코드 복잡성 증가
         *
         *    예시 (비권장):
         *    @Service
         *    class MyService {
         *        @Lazy
         *        @Autowired
         *        private MyService self;  // 자기 자신 주입
         *
         *        public void wrapper(String key) {
         *            self.getCachedData(key);  // 프록시를 통한 호출
         *        }
         *
         *        @Cacheable("data")
         *        public String getCachedData(String key) {...}
         *    }
         */

        // 가장 좋은 해결책: 별도 Bean으로 분리!
        assertTrue(true, "💡 캐시 로직을 별도 서비스로 분리하는 것이 가장 좋습니다!");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: 1
     * 테스트 2: 2
     * 테스트 3: 1, 2
     * 테스트 4: 2
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final int _____ = -999;
}
