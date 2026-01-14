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
 * 🔮 SpEL (Spring Expression Language) 학습 테스트
 * =============================================================================
 *
 * Spring Cache에서 사용할 수 있는 SpEL 표현식을 학습합니다.
 *
 * 💡 주요 SpEL 변수:
 *
 * 1. 파라미터 접근:
 *    - #paramName: 파라미터 이름으로 접근
 *    - #p0, #p1, ...: 인덱스로 접근 (p = parameter)
 *    - #a0, #a1, ...: 인덱스로 접근 (a = argument)
 *    - #root.args[0]: root 객체를 통한 접근
 *
 * 2. 결과값 접근:
 *    - #result: 메서드 반환값 (unless에서만 사용)
 *
 * 3. root 객체:
 *    - #root.methodName: 메서드 이름
 *    - #root.method: Method 객체
 *    - #root.targetClass: 대상 클래스
 *    - #root.target: 대상 객체
 *    - #root.caches: 사용되는 캐시들
 *
 * 4. 중첩 속성:
 *    - #user.id: User 객체의 id 속성
 *    - #user.address.city: 중첩된 속성 접근
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpELExpressionTest {

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
    // 📌 테스트 1: 파라미터 인덱스로 접근 (#p0, #a0)
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("SpEL: #p0으로 첫 번째 파라미터 접근")
    void test_SpEL_ParameterIndex() {
        // Given: findByIdUsingIndex()는 @Cacheable(key = "#p0") 사용

        // When: 같은 id로 두 번 호출
        userService.findByIdUsingIndex(1L);
        userService.findByIdUsingIndex(1L);

        // Then: #p0 = #id 이므로 캐시 히트
        // TODO: 빈칸을 채우세요
        assertEquals(_____, userService.getFindByIdCallCount(),
                "#p0이 첫 번째 파라미터를 정확히 참조하여 캐시 동작");
    }

    // =========================================================================
    // 📌 테스트 2: 객체 속성 접근 (#user.id)
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("SpEL: 객체의 속성으로 캐시 키 생성")
    void test_SpEL_ObjectProperty() {
        // Given: updateUser()는 @CachePut(key = "#user.id") 사용
        User user = new User(1L, "alice", "alice@example.com", 25);

        // When: 유저 업데이트
        userService.updateUser(user);

        // Then: user.id (= 1L)를 키로 캐시 저장됨
        Cache usersCache = cacheManager.getCache("users");

        // TODO: 빈칸을 채우세요
        // key="#user.id"이므로 캐시 키는 user.getId() = 1L
        boolean cachedWithUserId = (usersCache.get(1L) != null);
        assertEquals(_____, cachedWithUserId,
                "#user.id로 캐시 키가 생성되었나요?");
    }

    // =========================================================================
    // 📌 테스트 3: root.methodName 활용
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("SpEL: #root.methodName으로 메서드명 포함 키 생성")
    void test_SpEL_RootMethodName() {
        // Given: findByIdWithMethodName()은 @Cacheable(key = "#root.methodName + '_' + #id")

        // When: 호출
        userService.findByIdWithMethodName(1L);

        // Then: 캐시 키가 "findByIdWithMethodName_1"로 생성됨
        Cache usersCache = cacheManager.getCache("users");

        // 키는 "methodName_id" 형식
        String expectedKey = "findByIdWithMethodName_1";
        boolean cachedWithMethodNameKey = (usersCache.get(expectedKey) != null);

        // TODO: 빈칸을 채우세요
        assertEquals(_____, cachedWithMethodNameKey,
                "#root.methodName이 캐시 키에 포함되었나요?");
    }

    // =========================================================================
    // 📌 테스트 4: 복합 condition 표현식
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("SpEL: 복합 조건식 (AND, OR)")
    void test_SpEL_ComplexCondition() {
        // Given: findActiveUser()는
        // condition = "#id != null && #id > 0"
        // unless = "#result == null || !#result.active"

        // When: 유효한 id로 활성 사용자 조회 (id=1L, alice, active=true)
        userService.findActiveUser(1L);
        userService.findActiveUser(1L);
        int countForActiveUser = userService.getFindByIdCallCount();

        userService.resetCallCounts();

        // When: 비활성 사용자 조회 (id=3L, charlie, active=false)
        userService.findActiveUser(3L);
        userService.findActiveUser(3L);
        int countForInactiveUser = userService.getFindByIdCallCount();

        // Then:
        // - 활성 사용자: condition=true, unless=false -> 캐시됨 -> 1번만 실행
        // - 비활성 사용자: condition=true, unless=true -> 캐시 안됨 -> 2번 실행
        // TODO: 빈칸을 채우세요
        assertEquals(_____, countForActiveUser, "활성 사용자는 캐시됨");
        assertEquals(_____, countForInactiveUser, "비활성 사용자는 캐시 안됨 (unless 조건)");
    }

    // =========================================================================
    // 📌 테스트 5: null-safe 연산자 (?.)
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("SpEL: null-safe 연산자로 NPE 방지")
    void test_SpEL_NullSafe() {
        // Given: findActiveUser()의 unless = "#result == null || !#result.active"
        // #result?.active 형태로 null-safe 접근 가능

        // When: 존재하지 않는 id로 조회 (null 반환)
        userService.findActiveUser(999L);
        userService.findActiveUser(999L);

        // Then: result가 null이므로 unless 조건에 의해 캐시 안됨
        // TODO: 빈칸을 채우세요
        assertEquals(_____, userService.getFindByIdCallCount(),
                "null 결과는 캐시되지 않음 (unless 조건)");
    }

    // =========================================================================
    // 📌 테스트 6: 잘못된 condition (null id)
    // =========================================================================
    @Test
    @Order(6)
    @DisplayName("SpEL: condition이 false면 캐시 사용 안함")
    void test_SpEL_ConditionFalse() {
        // Given: condition = "#id != null && #id > 0"

        // When: id가 0이면 condition=false
        userService.addUser(new User(0L, "zero", "zero@example.com", 20));
        userService.findActiveUser(0L);
        userService.findActiveUser(0L);

        // Then: condition=false이므로 캐시 사용 X, 매번 실행
        // TODO: 빈칸을 채우세요
        assertEquals(_____, userService.getFindByIdCallCount(),
                "condition=false면 캐시를 전혀 사용하지 않음");
    }

    // =========================================================================
    // 📌 테스트 7: SpEL 요약 퀴즈
    // =========================================================================
    @Test
    @Order(7)
    @DisplayName("SpEL 퀴즈: 각 표현식의 의미는?")
    void test_SpEL_Quiz() {
        /*
         * 💡 SpEL 표현식 퀴즈!
         * 각 표현식이 무엇을 의미하는지 맞춰보세요.
         *
         * Q1: #isbn
         *     A: _______________ (힌트: 파라미터)
         *
         * Q2: #p0
         *     A: _______________ (힌트: 인덱스)
         *
         * Q3: #book.author
         *     A: _______________ (힌트: 중첩 속성)
         *
         * Q4: #root.methodName
         *     A: _______________ (힌트: 메서드)
         *
         * Q5: #result?.title
         *     A: _______________ (힌트: null-safe)
         *
         * Q6: #name.length() > 10
         *     A: _______________ (힌트: 메서드 호출)
         */

        // 정답은 아래 주석 참조!

        assertTrue(true, "퀴즈를 풀어보세요!");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 테스트 정답:
     * 테스트 1: 1
     * 테스트 2: true
     * 테스트 3: true
     * 테스트 4: 1, 2
     * 테스트 5: 2
     * 테스트 6: 2
     *
     * 퀴즈 정답:
     * Q1: isbn이라는 이름의 파라미터 값
     * Q2: 첫 번째 파라미터 (0번 인덱스)
     * Q3: book 객체의 author 속성값
     * Q4: 현재 실행 중인 메서드의 이름
     * Q5: result가 null이 아니면 title 속성, null이면 null
     * Q6: name 문자열의 길이가 10보다 큰지 (조건식)
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final int _____ = -999;
    private static final boolean ____B = false;
}
