package org.my.springcachetest.answer;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Book;
import org.my.springcachetest.service.AdvancedBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * 🔬 Spring Cache 심화 기능 - 정답 테스트
 * =============================================================================
 *
 * 이 테스트는 AdvancedCacheTest의 정답 버전입니다.
 * 모든 빈칸이 채워져 있으며, 테스트가 통과해야 합니다.
 *
 * 📚 학습한 내용 정리:
 *
 * 1. KeyGenerator
 *    - 기본: SimpleKeyGenerator (파라미터만으로 키 생성)
 *    - 커스텀: 클래스명, 메서드명 등 추가 정보 포함 가능
 *    - 비즈니스 로직에 맞는 키 생성 전략 구현
 *
 * 2. CacheResolver
 *    - 런타임에 동적으로 캐시 선택
 *    - 파라미터 값, 컨텍스트에 따라 다른 캐시 사용
 *
 * 3. CacheManager
 *    - 다중 캐시 매니저 설정
 *    - @Primary로 기본 매니저 지정
 *    - 메서드별로 다른 매니저 사용 가능
 *
 * 4. sync 옵션
 *    - true: 동시 요청 시 하나의 스레드만 실행 (Cache Stampede 방지)
 *    - false: 동시 요청 시 모든 스레드 실행 가능
 *
 * 5. @CacheConfig
 *    - 클래스 레벨 기본 설정
 *    - 메서드 레벨에서 재정의 가능
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdvancedCacheAnswerTest {

    @Autowired
    private AdvancedBookService advancedBookService;

    @Autowired
    @Qualifier("primaryCacheManager")
    private CacheManager primaryCacheManager;

    @Autowired
    @Qualifier("vipCacheManager")
    private CacheManager vipCacheManager;

    private static final String ISBN_EFFECTIVE_JAVA = "978-0-13-468599-1";
    private static final String ISBN_JAVASCRIPT = "978-0-596-51774-8";

    @BeforeEach
    void setUp() {
        primaryCacheManager.getCacheNames().forEach(name -> {
            Cache cache = primaryCacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        vipCacheManager.getCacheNames().forEach(name -> {
            Cache cache = vipCacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        advancedBookService.resetCallCount();
    }

    // =========================================================================
    // 📌 Part 1: KeyGenerator - 커스텀 키 생성
    // =========================================================================

    /**
     * 💡 학습 포인트:
     * customKeyGenerator는 "클래스명.메서드명:[params]" 형식으로 키를 생성합니다.
     * 이렇게 하면 서로 다른 메서드가 같은 파라미터로 호출되어도 키가 충돌하지 않습니다.
     */
    @Test
    @Order(1)
    @DisplayName("[정답] KeyGenerator: customKeyGenerator 키 형식 확인")
    void test_CustomKeyGenerator_KeyFormat() {
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA);

        Cache cache = primaryCacheManager.getCache("advancedBooks");
        assertNotNull(cache);

        String expectedKey = "AdvancedBookService.findWithCustomKeyGenerator:[" + ISBN_EFFECTIVE_JAVA + "]";
        Cache.ValueWrapper cached = cache.get(expectedKey);

        // ✅ 정답: 커스텀 키 형식으로 캐시에 저장됨
        assertNotNull(cached, "커스텀 키 형식으로 캐시가 저장되어야 합니다");
    }

    /**
     * 💡 학습 포인트:
     * 기본 SimpleKeyGenerator는 파라미터만으로 키를 생성하므로,
     * 같은 파라미터를 사용하는 다른 메서드는 키가 충돌할 수 있습니다.
     * customKeyGenerator는 메서드명을 포함하여 이 문제를 해결합니다.
     */
    @Test
    @Order(2)
    @DisplayName("[정답] KeyGenerator: 같은 파라미터, 다른 메서드 = 다른 키")
    void test_CustomKeyGenerator_DifferentMethods() {
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA);
        advancedBookService.findWithClassLevelConfig(ISBN_EFFECTIVE_JAVA);

        // ✅ 정답: 2 (메서드명이 키에 포함되므로 둘 다 실행됨)
        assertEquals(2, advancedBookService.getCallCount(),
                "메서드명이 키에 포함되므로 둘 다 실행되어야 합니다");
    }

    /**
     * 💡 학습 포인트:
     * isbnKeyGenerator는 ISBN의 마지막 문자(체크섬)를 제외하고 키를 생성합니다.
     * "978-0-13-468599-1"과 "978-0-13-468599-X"는 같은 키 "978-0-13-468599-"가 됩니다.
     * 이는 비즈니스 요구사항에 맞게 키 생성 로직을 커스터마이징하는 예시입니다.
     */
    @Test
    @Order(3)
    @DisplayName("[정답] KeyGenerator: isbnKeyGenerator 체크섬 제외")
    void test_IsbnKeyGenerator() {
        String isbn1 = "978-0-13-468599-1";
        String isbn2 = "978-0-13-468599-X";

        advancedBookService.addBook(isbn2, new Book(isbn2, "Test Book", "Test Author", 10000));

        Book first = advancedBookService.findWithIsbnKeyGenerator(isbn1);
        Book second = advancedBookService.findWithIsbnKeyGenerator(isbn2);

        // ✅ 정답: 1 (체크섬을 제외한 키가 같으므로 캐시 히트)
        assertEquals(1, advancedBookService.getCallCount(),
                "체크섬을 제외한 키가 같으므로 캐시 히트");

        assertEquals(first.getIsbn(), second.getIsbn());
    }

    /**
     * 💡 학습 포인트:
     * compositeKeyGenerator는 여러 파라미터를 "param1_param2" 형식으로 조합합니다.
     * 파라미터 순서가 다르면 다른 키가 생성됩니다.
     */
    @Test
    @Order(4)
    @DisplayName("[정답] KeyGenerator: compositeKeyGenerator 파라미터 조합")
    void test_CompositeKeyGenerator() {
        advancedBookService.findWithCompositeKey("Effective Java", "Joshua Bloch");
        int countAfterFirst = advancedBookService.getCallCount();

        advancedBookService.findWithCompositeKey("Effective Java", "Joshua Bloch");
        int countAfterSecond = advancedBookService.getCallCount();

        advancedBookService.findWithCompositeKey("Joshua Bloch", "Effective Java");
        int countAfterThird = advancedBookService.getCallCount();

        // ✅ 정답: 1, 1, 2
        assertEquals(1, countAfterFirst, "첫 호출은 메서드 실행");
        assertEquals(1, countAfterSecond, "같은 파라미터는 캐시 히트");
        assertEquals(2, countAfterThird, "순서가 다르면 키가 달라 캐시 미스");
    }

    // =========================================================================
    // 📌 Part 2: CacheResolver - 런타임 캐시 결정
    // =========================================================================

    /**
     * 💡 학습 포인트:
     * CacheResolver는 런타임에 어떤 캐시를 사용할지 결정합니다.
     * runtimeCacheResolver는 파라미터에 "VIP"가 포함되면 vipBooks 캐시를,
     * 그렇지 않으면 advancedBooks 캐시를 사용합니다.
     */
    @Test
    @Order(5)
    @DisplayName("[정답] CacheResolver: 파라미터 기반 캐시 선택")
    void test_RuntimeCacheResolver() {
        String normalId = ISBN_EFFECTIVE_JAVA;
        String vipId = "VIP-001";

        advancedBookService.findWithRuntimeCacheResolver(normalId);

        Cache advancedCache = primaryCacheManager.getCache("advancedBooks");
        Cache vipCache = vipCacheManager.getCache("vipBooks");

        // ✅ 정답: 일반 ID는 advancedBooks에 저장
        assertNotNull(advancedCache.get(normalId), "일반 ID는 advancedBooks 캐시에 저장");
        assertNull(vipCache.get(normalId), "일반 ID는 vipBooks 캐시에 저장되지 않음");

        advancedBookService.findWithRuntimeCacheResolver(vipId);

        // ✅ 정답: VIP ID는 vipBooks에 저장
        assertNotNull(vipCache.get(vipId), "VIP ID는 vipBooks 캐시에 저장");
    }

    /**
     * 💡 학습 포인트:
     * 같은 메서드라도 CacheResolver에 의해 다른 캐시에 저장될 수 있습니다.
     * 각 캐시에서 캐시 히트가 발생하므로 총 실행 횟수는 캐시 종류 수와 같습니다.
     */
    @Test
    @Order(6)
    @DisplayName("[정답] CacheResolver: 같은 메서드, 다른 캐시")
    void test_RuntimeCacheResolver_SameMethodDifferentCache() {
        String normalId = ISBN_EFFECTIVE_JAVA;
        String vipId = "VIP-001";

        advancedBookService.findWithRuntimeCacheResolver(normalId);
        advancedBookService.findWithRuntimeCacheResolver(normalId);

        advancedBookService.findWithRuntimeCacheResolver(vipId);
        advancedBookService.findWithRuntimeCacheResolver(vipId);

        // ✅ 정답: 2 (일반 1번 + VIP 1번)
        assertEquals(2, advancedBookService.getCallCount(),
                "서로 다른 캐시지만, 각각 캐시 히트하여 총 2번 실행");
    }

    // =========================================================================
    // 📌 Part 3: CacheManager - 다중 캐시 매니저
    // =========================================================================

    /**
     * 💡 학습 포인트:
     * 여러 CacheManager를 정의하고, 메서드별로 어떤 매니저를 사용할지 지정할 수 있습니다.
     * @Primary로 지정된 매니저가 기본값이며, cacheManager 속성으로 다른 매니저를 지정합니다.
     */
    @Test
    @Order(7)
    @DisplayName("[정답] CacheManager: 특정 캐시 매니저 지정")
    void test_SpecificCacheManager() {
        advancedBookService.findVipBook("VIP-001");

        Cache vipCache = vipCacheManager.getCache("vipBooks");

        // ✅ 정답: vipCacheManager의 캐시에 저장됨
        assertNotNull(vipCache.get("VIP-001"), "vipCacheManager의 캐시에 저장됨");
    }

    /**
     * 💡 학습 포인트:
     * 서로 다른 CacheManager의 캐시는 완전히 독립적입니다.
     * 같은 키라도 다른 매니저의 캐시에 저장되면 캐시 히트가 발생하지 않습니다.
     */
    @Test
    @Order(8)
    @DisplayName("[정답] CacheManager: 캐시 격리")
    void test_CacheManagerIsolation() {
        advancedBookService.findNormalBook(ISBN_EFFECTIVE_JAVA);
        advancedBookService.findVipBook(ISBN_EFFECTIVE_JAVA);

        // ✅ 정답: 2 (서로 다른 CacheManager이므로 둘 다 실행)
        assertEquals(2, advancedBookService.getCallCount(),
                "서로 다른 CacheManager이므로 둘 다 실행됨");
    }

    // =========================================================================
    // 📌 Part 4: sync 옵션 - 동시성 제어
    // =========================================================================

    /**
     * 💡 학습 포인트:
     * sync=true는 동시에 같은 키로 요청이 들어올 때 하나의 스레드만 메서드를 실행합니다.
     * 나머지 스레드는 첫 번째 스레드가 완료되어 캐시에 저장할 때까지 대기합니다.
     * 이는 "Cache Stampede" 현상을 방지합니다.
     *
     * ⚠️ Cache Stampede란?
     * 캐시 미스가 발생했을 때 동시에 여러 요청이 DB를 조회하는 현상입니다.
     * 특히 캐시 만료 시점에 많은 요청이 몰리면 DB에 큰 부하를 줄 수 있습니다.
     */
    @Test
    @Order(9)
    @DisplayName("[정답] sync=true: Cache Stampede 방지")
    void test_SyncTrue_SingleExecution() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        List<Future<Book>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startLatch.await();
                Book result = advancedBookService.findWithSync(ISBN_EFFECTIVE_JAVA);
                endLatch.countDown();
                return result;
            }));
        }

        startLatch.countDown();
        endLatch.await(5, TimeUnit.SECONDS);

        // ✅ 정답: 1 (sync=true면 하나의 스레드만 실행)
        assertEquals(1, advancedBookService.getSyncCallCount(),
                "sync=true면 하나의 스레드만 메서드 실행, 나머지는 캐시 대기");

        Book expectedBook = futures.get(0).get();
        for (Future<Book> future : futures) {
            assertEquals(expectedBook.getIsbn(), future.get().getIsbn());
        }

        executor.shutdown();
    }

    /**
     * 💡 학습 포인트:
     * sync=false (기본값)는 동시 요청 시 모든 스레드가 메서드를 실행할 수 있습니다.
     * 이는 "Cache Stampede"를 일으킬 수 있어 주의가 필요합니다.
     */
    @Test
    @Order(10)
    @DisplayName("[정답] sync=false: Cache Stampede 발생 가능")
    void test_SyncFalse_MultipleExecutions() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    advancedBookService.findWithoutSync(ISBN_JAVASCRIPT);
                    endLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(5, TimeUnit.SECONDS);

        // ✅ 정답: 1 이상 (타이밍에 따라 다름, 보통 여러 번 실행됨)
        int actualCallCount = advancedBookService.getSyncCallCount();
        assertTrue(actualCallCount >= 1,
                "sync=false면 동시 요청 시 여러 스레드가 메서드를 실행할 수 있음");

        System.out.println("sync=false 시 실제 메서드 실행 횟수: " + actualCallCount);
        System.out.println("💡 이것이 Cache Stampede 현상입니다!");

        executor.shutdown();
    }

    // =========================================================================
    // 📌 Part 5: @CacheConfig - 클래스 레벨 설정
    // =========================================================================

    /**
     * 💡 학습 포인트:
     * @CacheConfig는 클래스의 모든 캐시 메서드에 적용되는 기본 설정을 정의합니다.
     * cacheNames, keyGenerator, cacheManager, cacheResolver 등을 지정할 수 있습니다.
     * 메서드 레벨에서 재정의하지 않으면 이 기본값이 사용됩니다.
     */
    @Test
    @Order(11)
    @DisplayName("[정답] @CacheConfig: 클래스 레벨 기본 설정")
    void test_CacheConfig_DefaultCacheName() {
        advancedBookService.findWithClassLevelConfig(ISBN_EFFECTIVE_JAVA);

        Cache cache = primaryCacheManager.getCache("advancedBooks");

        // ✅ 정답: @CacheConfig의 기본 캐시 이름 적용
        assertNotNull(cache.get(ISBN_EFFECTIVE_JAVA),
                "@CacheConfig의 기본 캐시 이름이 적용되어야 합니다");
    }

    /**
     * 💡 학습 포인트:
     * 메서드 레벨의 설정은 @CacheConfig의 클래스 레벨 설정보다 우선합니다.
     * 이를 통해 클래스 전체에 기본값을 적용하고, 특정 메서드만 다르게 설정할 수 있습니다.
     */
    @Test
    @Order(12)
    @DisplayName("[정답] @CacheConfig: 메서드 레벨 재정의")
    void test_CacheConfig_MethodOverride() {
        advancedBookService.findWithMethodLevelOverride(ISBN_EFFECTIVE_JAVA);

        Cache advancedCache = primaryCacheManager.getCache("advancedBooks");
        Cache booksCache = primaryCacheManager.getCache("books");

        // ✅ 정답: 메서드 레벨 설정이 우선
        assertNull(advancedCache.get(ISBN_EFFECTIVE_JAVA),
                "메서드 레벨에서 재정의되어 advancedBooks에는 없음");
        assertNotNull(booksCache.get(ISBN_EFFECTIVE_JAVA),
                "메서드 레벨 설정으로 books 캐시에 저장됨");
    }

    // =========================================================================
    // 📌 보너스: 실용 가이드
    // =========================================================================

    @Test
    @Order(13)
    @DisplayName("[정답] 보너스: 기본 키 vs 커스텀 키 비교")
    void test_DefaultKeyVsCustomKey() {
        advancedBookService.findNormalBook(ISBN_EFFECTIVE_JAVA);
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA);

        // ✅ 정답: 2 (서로 다른 캐시 또는 다른 키)
        assertEquals(2, advancedBookService.getCallCount(),
                "기본 키와 커스텀 키는 다르므로 둘 다 실행됨");
    }

    // =========================================================================
    // 📚 정답 요약
    // =========================================================================
    /*
     * 테스트 1: assertNotNull (커스텀 키 형식으로 저장됨)
     * 테스트 2: 2 (메서드명이 키에 포함)
     * 테스트 3: 1 (체크섬 제외 키가 같음)
     * 테스트 4: 1, 1, 2
     * 테스트 5: assertNotNull, assertNull, assertNotNull
     * 테스트 6: 2 (각 캐시별 1번씩)
     * 테스트 7: assertNotNull
     * 테스트 8: 2 (독립적인 캐시 매니저)
     * 테스트 9: 1 (sync=true)
     * 테스트 10: >= 1 (sync=false, Cache Stampede)
     * 테스트 11: assertNotNull
     * 테스트 12: assertNull, assertNotNull
     * 테스트 13: 2
     *
     * 💡 핵심 요약:
     *
     * 1. KeyGenerator
     *    - 기본 SimpleKeyGenerator: 파라미터로만 키 생성
     *    - 커스텀 KeyGenerator: 충돌 방지, 디버깅 용이
     *
     * 2. CacheResolver
     *    - 런타임에 동적으로 캐시 선택
     *    - 비즈니스 로직에 따른 캐시 분리
     *
     * 3. CacheManager
     *    - 다중 캐시 매니저로 다른 전략 적용
     *    - @Primary로 기본 매니저 지정
     *
     * 4. sync=true
     *    - Cache Stampede 방지
     *    - condition, unless와 함께 사용 불가
     *
     * 5. @CacheConfig
     *    - 클래스 레벨 기본 설정
     *    - 메서드 레벨에서 재정의 가능
     */
}
