package org.my.springcachetest;

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
 * 🔬 Spring Cache 심화 기능 학습 테스트
 * =============================================================================
 *
 * 이 테스트를 통해 다음 심화 기능들을 학습합니다:
 *
 * 1. keyGenerator - 커스텀 키 생성 전략
 * 2. cacheResolver - 런타임 캐시 결정
 * 3. cacheManager - 다중 캐시 매니저
 * 4. sync - 동시성 제어 (Cache Stampede 방지)
 * 5. @CacheConfig - 클래스 레벨 설정
 *
 * 💡 빈칸(_____)을 채워서 테스트를 통과시켜 보세요!
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdvancedCacheTest {

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
        // 캐시와 카운터 초기화
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

    @Test
    @Order(1)
    @DisplayName("KeyGenerator: customKeyGenerator는 클래스명.메서드명:[params] 형식의 키 생성")
    void test_CustomKeyGenerator_KeyFormat() {
        // Given: customKeyGenerator를 사용하는 메서드
        // 키 형식: "AdvancedBookService.findWithCustomKeyGenerator:[978-0-13-468599-1]"

        // When: 첫 번째 호출
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA);

        // Then: advancedBooks 캐시에서 키 확인
        Cache cache = primaryCacheManager.getCache("advancedBooks");
        assertNotNull(cache);

        // 커스텀 키 형식으로 저장되었는지 확인
        String expectedKey = "AdvancedBookService.findWithCustomKeyGenerator:[" + ISBN_EFFECTIVE_JAVA + "]";
        Cache.ValueWrapper cached = cache.get(expectedKey);

        // TODO: 빈칸을 채우세요
        // 커스텀 KeyGenerator가 생성한 키로 캐시가 저장되었나요?
        assertNotNull(cached, "커스텀 키 형식으로 캐시가 저장되어야 합니다");
    }

    @Test
    @Order(2)
    @DisplayName("KeyGenerator: 같은 파라미터도 메서드가 다르면 키가 다름")
    void test_CustomKeyGenerator_DifferentMethods() {
        // Given: customKeyGenerator는 메서드명을 키에 포함

        // When: 같은 ISBN으로 다른 메서드 호출
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA);  // 메서드 A
        advancedBookService.findWithClassLevelConfig(ISBN_EFFECTIVE_JAVA);    // 메서드 B (다른 키)

        // Then: 두 메서드 모두 실제로 실행됨 (키가 다르므로 캐시 미스)
        // TODO: 빈칸을 채우세요
        // customKeyGenerator는 메서드명을 포함하므로, 같은 파라미터도 키가 다릅니다
        assertEquals(2, advancedBookService.getCallCount(),
                "메서드명이 키에 포함되므로 둘 다 실행되어야 합니다");
    }

    @Test
    @Order(3)
    @DisplayName("KeyGenerator: isbnKeyGenerator는 체크섬을 제외한 키 생성")
    void test_IsbnKeyGenerator() {
        // Given: isbnKeyGenerator는 ISBN의 마지막 문자(체크섬)를 제외
        String isbn1 = "978-0-13-468599-1";  // 체크섬: 1
        String isbn2 = "978-0-13-468599-X";  // 체크섬: X (다름)
        // 두 ISBN의 키: "978-0-13-468599-" (같음!)

        // isbn2에 해당하는 책 추가
        advancedBookService.addBook(isbn2, new Book(isbn2, "Test Book", "Test Author", 10000));

        // When: 다른 ISBN이지만 체크섬만 다른 경우
        Book first = advancedBookService.findWithIsbnKeyGenerator(isbn1);
        Book second = advancedBookService.findWithIsbnKeyGenerator(isbn2);

        // Then: 키가 같으므로 두 번째는 캐시 히트!
        // TODO: 빈칸을 채우세요
        // isbnKeyGenerator가 체크섬을 제외하면 두 ISBN의 키가 같아집니다
        assertEquals(1, advancedBookService.getCallCount(),
                "체크섬을 제외한 키가 같으므로 캐시 히트");

        // 두 번째 호출은 첫 번째의 캐시된 결과를 반환
        assertEquals(first.getIsbn(), second.getIsbn());
    }

    @Test
    @Order(4)
    @DisplayName("KeyGenerator: compositeKeyGenerator는 여러 파라미터를 조합")
    void test_CompositeKeyGenerator() {
        // Given: compositeKeyGenerator는 "param1_param2" 형식의 키 생성

        // When: 두 파라미터로 호출
        advancedBookService.findWithCompositeKey("Effective Java", "Joshua Bloch");
        int countAfterFirst = advancedBookService.getCallCount();

        // 같은 파라미터로 다시 호출
        advancedBookService.findWithCompositeKey("Effective Java", "Joshua Bloch");
        int countAfterSecond = advancedBookService.getCallCount();

        // 순서를 바꿔서 호출 (다른 키!)
        advancedBookService.findWithCompositeKey("Joshua Bloch", "Effective Java");
        int countAfterThird = advancedBookService.getCallCount();

        // Then:
        // TODO: 빈칸을 채우세요
        assertEquals(1, countAfterFirst, "첫 호출은 메서드 실행");
        assertEquals(1, countAfterSecond, "같은 파라미터는 캐시 히트");
        assertEquals(2, countAfterThird, "순서가 다르면 키가 달라 캐시 미스");
    }

    // =========================================================================
    // 📌 Part 2: CacheResolver - 런타임 캐시 결정
    // =========================================================================

    @Test
    @Order(5)
    @DisplayName("CacheResolver: 파라미터에 따라 다른 캐시 사용")
    void test_RuntimeCacheResolver() {
        // Given: runtimeCacheResolver는 "VIP" 포함 여부로 캐시 결정
        String normalId = ISBN_EFFECTIVE_JAVA;
        String vipId = "VIP-001";

        // When: 일반 ID로 호출
        advancedBookService.findWithRuntimeCacheResolver(normalId);

        // Then: advancedBooks 캐시에 저장됨
        Cache advancedCache = primaryCacheManager.getCache("advancedBooks");
        Cache vipCache = vipCacheManager.getCache("vipBooks");

        // TODO: 빈칸을 채우세요
        assertNotNull(advancedCache.get(normalId), "일반 ID는 advancedBooks 캐시에 저장");
        assertNull(vipCache.get(normalId), "일반 ID는 vipBooks 캐시에 저장되지 않음");

        // When: VIP ID로 호출
        advancedBookService.findWithRuntimeCacheResolver(vipId);

        // Then: vipBooks 캐시에 저장됨
        // TODO: 빈칸을 채우세요
        assertNotNull(vipCache.get(vipId), "VIP ID는 vipBooks 캐시에 저장");
    }

    @Test
    @Order(6)
    @DisplayName("CacheResolver: 같은 메서드도 파라미터에 따라 다른 캐시 사용")
    void test_RuntimeCacheResolver_SameMethodDifferentCache() {
        // Given: 같은 findWithRuntimeCacheResolver 메서드
        String normalId = ISBN_EFFECTIVE_JAVA;
        String vipId = "VIP-001";

        // When: 일반 ID와 VIP ID로 각각 2번씩 호출
        advancedBookService.findWithRuntimeCacheResolver(normalId);
        advancedBookService.findWithRuntimeCacheResolver(normalId);  // 캐시 히트

        advancedBookService.findWithRuntimeCacheResolver(vipId);
        advancedBookService.findWithRuntimeCacheResolver(vipId);  // 캐시 히트

        // Then: 각각 한 번씩만 실제 메서드 실행
        // TODO: 빈칸을 채우세요
        assertEquals(2, advancedBookService.getCallCount(),
                "서로 다른 캐시지만, 각각 캐시 히트하여 총 2번 실행");
    }

    // =========================================================================
    // 📌 Part 3: CacheManager - 다중 캐시 매니저
    // =========================================================================

    @Test
    @Order(7)
    @DisplayName("CacheManager: 특정 캐시 매니저 지정")
    void test_SpecificCacheManager() {
        // Given: findVipBook은 vipCacheManager를 사용

        // When: VIP 책 조회
        advancedBookService.findVipBook("VIP-001");

        // Then: vipCacheManager의 vipBooks 캐시에만 저장됨
        Cache vipCache = vipCacheManager.getCache("vipBooks");
        Cache primaryCache = primaryCacheManager.getCache("vipBooks");  // primary에는 없음

        // TODO: 빈칸을 채우세요
        assertNotNull(vipCache.get("VIP-001"), "vipCacheManager의 캐시에 저장됨");
        // primaryCacheManager의 vipBooks 캐시는 존재하지 않음
    }

    @Test
    @Order(8)
    @DisplayName("CacheManager: 서로 다른 매니저의 캐시는 독립적")
    void test_CacheManagerIsolation() {
        // Given: 같은 ISBN이지만 다른 CacheManager 사용

        // When: 일반 조회와 VIP 조회
        advancedBookService.findNormalBook(ISBN_EFFECTIVE_JAVA);  // primary
        advancedBookService.findVipBook(ISBN_EFFECTIVE_JAVA);     // vip

        // Then: 둘 다 실제 메서드 실행 (캐시가 독립적이므로)
        // TODO: 빈칸을 채우세요
        assertEquals(2, advancedBookService.getCallCount(),
                "서로 다른 CacheManager이므로 둘 다 실행됨");
    }

    // =========================================================================
    // 📌 Part 4: sync 옵션 - 동시성 제어
    // =========================================================================

    @Test
    @Order(9)
    @DisplayName("sync=true: 동시 요청 시 하나의 스레드만 메서드 실행")
    void test_SyncTrue_SingleExecution() throws Exception {
        // Given: sync=true로 설정된 findWithSync 메서드
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        List<Future<Book>> futures = new ArrayList<>();

        // When: 10개 스레드가 동시에 같은 키로 요청
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startLatch.await();  // 모든 스레드가 동시에 시작
                Book result = advancedBookService.findWithSync(ISBN_EFFECTIVE_JAVA);
                endLatch.countDown();
                return result;
            }));
        }

        startLatch.countDown();  // 동시 시작!
        endLatch.await(5, TimeUnit.SECONDS);

        // Then: sync=true이므로 실제 메서드는 1번만 실행
        // TODO: 빈칸을 채우세요
        assertEquals(1, advancedBookService.getSyncCallCount(),
                "sync=true면 하나의 스레드만 메서드 실행, 나머지는 캐시 대기");

        // 모든 스레드가 같은 결과를 받음
        Book expectedBook = futures.get(0).get();
        for (Future<Book> future : futures) {
            assertEquals(expectedBook.getIsbn(), future.get().getIsbn());
        }

        executor.shutdown();
    }

    @Test
    @Order(10)
    @DisplayName("sync=false: 동시 요청 시 모든 스레드가 메서드 실행 (Cache Stampede)")
    void test_SyncFalse_MultipleExecutions() throws Exception {
        // Given: sync=false (기본값)인 findWithoutSync 메서드
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // When: 10개 스레드가 동시에 같은 키로 요청
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

        startLatch.countDown();  // 동시 시작!
        endLatch.await(5, TimeUnit.SECONDS);

        // Then: sync=false이므로 여러 스레드가 동시에 메서드 실행 가능
        // 정확한 숫자는 타이밍에 따라 다르지만, 1보다 큼
        // TODO: 빈칸을 채우세요
        int actualCallCount = advancedBookService.getSyncCallCount();
        assertTrue(actualCallCount >= 1,
                "sync=false면 동시 요청 시 여러 스레드가 메서드를 실행할 수 있음");

        // 💡 실제로는 대부분의 스레드가 실행될 것입니다
        // 이것이 바로 "Cache Stampede" 현상입니다!
        System.out.println("sync=false 시 실제 메서드 실행 횟수: " + actualCallCount);

        executor.shutdown();
    }

    // =========================================================================
    // 📌 Part 5: @CacheConfig - 클래스 레벨 설정
    // =========================================================================

    @Test
    @Order(11)
    @DisplayName("@CacheConfig: 클래스 레벨 기본 캐시 이름 사용")
    void test_CacheConfig_DefaultCacheName() {
        // Given: @CacheConfig(cacheNames = "advancedBooks")가 클래스에 설정됨
        // findWithClassLevelConfig는 @Cacheable만 있고 cacheNames 미지정

        // When: 메서드 호출
        advancedBookService.findWithClassLevelConfig(ISBN_EFFECTIVE_JAVA);

        // Then: @CacheConfig의 기본값 "advancedBooks" 캐시에 저장됨
        Cache cache = primaryCacheManager.getCache("advancedBooks");

        // TODO: 빈칸을 채우세요
        assertNotNull(cache.get(ISBN_EFFECTIVE_JAVA),
                "@CacheConfig의 기본 캐시 이름이 적용되어야 합니다");
    }

    @Test
    @Order(12)
    @DisplayName("@CacheConfig: 메서드 레벨에서 재정의 가능")
    void test_CacheConfig_MethodOverride() {
        // Given: @CacheConfig(cacheNames = "advancedBooks")
        // findWithMethodLevelOverride는 @Cacheable(cacheNames = "books")로 재정의

        // When: 메서드 호출
        advancedBookService.findWithMethodLevelOverride(ISBN_EFFECTIVE_JAVA);

        // Then: 메서드 레벨 설정이 우선하여 "books" 캐시에 저장됨
        Cache advancedCache = primaryCacheManager.getCache("advancedBooks");
        Cache booksCache = primaryCacheManager.getCache("books");

        // TODO: 빈칸을 채우세요
        assertNull(advancedCache.get(ISBN_EFFECTIVE_JAVA),
                "메서드 레벨에서 재정의되어 advancedBooks에는 없음");
        assertNotNull(booksCache.get(ISBN_EFFECTIVE_JAVA),
                "메서드 레벨 설정으로 books 캐시에 저장됨");
    }

    // =========================================================================
    // 📌 보너스: 키 생성 전략 비교
    // =========================================================================

    @Test
    @Order(13)
    @DisplayName("보너스: 기본 키 vs 커스텀 키 비교")
    void test_DefaultKeyVsCustomKey() {
        // Given: 기본 키 생성과 커스텀 키 생성의 차이

        // 기본 키 생성 (SimpleKeyGenerator)
        // - 파라미터만으로 키 생성
        // - 같은 파라미터면 같은 키

        // 커스텀 키 생성 (customKeyGenerator)
        // - 클래스명.메서드명:[params] 형식
        // - 같은 파라미터도 메서드가 다르면 다른 키

        // When: 기본 키 사용 메서드와 커스텀 키 사용 메서드 호출
        advancedBookService.findNormalBook(ISBN_EFFECTIVE_JAVA);          // 기본 키: ISBN
        advancedBookService.findWithCustomKeyGenerator(ISBN_EFFECTIVE_JAVA); // 커스텀 키: 클래스.메서드:[ISBN]

        // Then: 둘 다 실행됨 (서로 다른 캐시 또는 다른 키)
        // TODO: 빈칸을 채우세요
        assertEquals(2, advancedBookService.getCallCount(),
                "기본 키와 커스텀 키는 다르므로 둘 다 실행됨");

        // 💡 정리:
        // - 기본 키: 간단하지만 메서드 간 충돌 가능
        // - 커스텀 키: 충돌 방지, 디버깅 용이
    }

    // =========================================================================
    // 💡 빈칸 플레이스홀더 (이 변수를 실제 값으로 바꾸세요)
    // =========================================================================
    private static final int _____ = -999;  // TODO: 실제 값으로 교체하세요
}
