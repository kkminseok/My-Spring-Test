package org.my.springcachetest;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Product;
import org.my.springcachetest.scenario.ProductCacheScenario;
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
 * 🧪 시나리오 검증 테스트
 * =============================================================================
 *
 * ProductCacheScenario의 각 메서드에 올바른 캐시 어노테이션을 작성했는지 검증합니다.
 * 테스트가 통과하면 정답입니다!
 *
 * 💡 실행 방법:
 * ./gradlew test --tests "*.ScenarioTest"
 *
 * 💡 개별 시나리오 테스트:
 * ./gradlew test --tests "*.ScenarioTest.scenario1*"
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ScenarioTest {

    @Autowired
    private ProductCacheScenario scenario;

    @Autowired
    @Qualifier("primaryCacheManager")
    private CacheManager primaryCacheManager;

    @Autowired
    @Qualifier("vipCacheManager")
    private CacheManager vipCacheManager;

    @BeforeEach
    void setUp() {
        // 캐시 초기화
        primaryCacheManager.getCacheNames().forEach(name -> {
            Cache cache = primaryCacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        vipCacheManager.getCacheNames().forEach(name -> {
            Cache cache = vipCacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
        scenario.resetCallCount();
    }

    // =========================================================================
    // 📌 시나리오 1: 기본 캐싱
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("시나리오1: 기본 캐싱 - 같은 ID로 2번 조회하면 메서드는 1번만 실행")
    void scenario1_BasicCaching() {
        // When: 같은 상품을 2번 조회
        scenario.findProduct(1L);
        scenario.findProduct(1L);

        // Then: 두 번째는 캐시 히트, 메서드는 1번만 실행
        assertEquals(1, scenario.getCallCount(),
                "❌ @Cacheable(\"products\")를 추가했나요?");
    }

    @Test
    @Order(2)
    @DisplayName("시나리오1: 기본 캐싱 - 다른 ID는 캐시 미스")
    void scenario1_BasicCaching_DifferentId() {
        // When: 다른 상품 조회
        scenario.findProduct(1L);
        scenario.findProduct(2L);

        // Then: 각각 캐시 미스, 메서드 2번 실행
        assertEquals(2, scenario.getCallCount(),
                "다른 ID는 별도로 캐시되어야 합니다");
    }

    // =========================================================================
    // 📌 시나리오 2: 조건부 캐싱 (condition)
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("시나리오2: 조건부 캐싱 - 고가 상품(>=100,000원)만 캐시")
    void scenario2_ConditionalCaching() {
        // Given:
        // - 상품1(맥북): 3,500,000원 -> 캐시 O
        // - 상품5(USB허브): 15,000원 -> 캐시 X

        // When: 고가 상품 2번 조회
        scenario.findExpensiveProduct(1L);  // 맥북 3,500,000원
        scenario.findExpensiveProduct(1L);
        int countAfterExpensive = scenario.getCallCount();

        scenario.resetCallCount();

        // When: 저가 상품 2번 조회
        scenario.findExpensiveProduct(5L);  // USB허브 15,000원
        scenario.findExpensiveProduct(5L);
        int countAfterCheap = scenario.getCallCount();

        // Then:
        assertEquals(1, countAfterExpensive,
                "❌ 고가 상품(>=100,000원)은 캐시되어야 합니다");
        assertEquals(2, countAfterCheap,
                "❌ 저가 상품은 캐시되지 않아야 합니다. condition 조건을 확인하세요");
    }

    // =========================================================================
    // 📌 시나리오 3: 결과 기반 제외 (unless)
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("시나리오3: 품절 상품 캐시 제외")
    void scenario3_UnlessSoldOut() {
        // Given:
        // - 상품2(키보드): 재고 50개 -> 캐시 O
        // - 상품3(마우스): 재고 0개 (품절) -> 캐시 X

        // When: 재고 있는 상품 2번 조회
        scenario.findAvailableProduct(2L);  // 키보드, 재고 있음
        scenario.findAvailableProduct(2L);
        int countAfterAvailable = scenario.getCallCount();

        scenario.resetCallCount();

        // When: 품절 상품 2번 조회
        scenario.findAvailableProduct(3L);  // 마우스, 품절
        scenario.findAvailableProduct(3L);
        int countAfterSoldOut = scenario.getCallCount();

        // Then:
        assertEquals(1, countAfterAvailable,
                "❌ 재고 있는 상품은 캐시되어야 합니다");
        assertEquals(2, countAfterSoldOut,
                "❌ 품절 상품은 캐시되지 않아야 합니다. unless 조건을 확인하세요");
    }

    @Test
    @Order(5)
    @DisplayName("시나리오3: null 결과 캐시 제외")
    void scenario3_UnlessNull() {
        // When: 존재하지 않는 상품 2번 조회
        scenario.findAvailableProduct(999L);
        scenario.findAvailableProduct(999L);

        // Then: null도 캐시 안 됨
        assertEquals(2, scenario.getCallCount(),
                "❌ null 결과는 캐시되지 않아야 합니다");
    }

    // =========================================================================
    // 📌 시나리오 4: 복합 키 생성
    // =========================================================================
    @Test
    @Order(6)
    @DisplayName("시나리오4: 복합 키 - 고객ID_상품ID 형태")
    void scenario4_CompositeKey() {
        // When: 같은 상품, 같은 고객 2번 조회
        scenario.findProductForCustomer(1L, 2L);  // 키: "1_2"
        scenario.findProductForCustomer(1L, 2L);  // 키: "1_2" - 캐시 히트
        int countAfterSameKey = scenario.getCallCount();

        // When: 다른 고객으로 호출 (같은 상품이지만 키가 다름)
        scenario.findProductForCustomer(2L, 2L);  // 키: "2_2" - 다른 키!
        int countAfterDifferentCustomer = scenario.getCallCount();

        // Then:
        assertEquals(1, countAfterSameKey,
                "❌ 같은 고객ID_상품ID는 캐시 히트해야 합니다");
        assertEquals(2, countAfterDifferentCustomer,
                "❌ 다른 고객은 다른 키이므로 캐시 미스해야 합니다. key=\"#customerId + '_' + #productId\" 형태인가요?");
    }

    // =========================================================================
    // 📌 시나리오 5: 동시성 제어 (sync)
    // =========================================================================
    @Test
    @Order(7)
    @DisplayName("시나리오5: sync=true - 동시 요청 시 메서드 1번만 실행")
    void scenario5_SyncTrue() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // When: 10개 스레드가 동시에 같은 상품 조회
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    scenario.findPopularProduct(1L);
                    endLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startLatch.countDown();  // 동시 시작!
        endLatch.await(5, TimeUnit.SECONDS);

        // Then: sync=true이므로 메서드는 1번만 실행
        assertEquals(1, scenario.getCallCount(),
                "❌ sync=true이면 동시 요청 시 하나의 스레드만 메서드를 실행해야 합니다");

        executor.shutdown();
    }

    // =========================================================================
    // 📌 시나리오 6: 캐시 갱신 (@CachePut)
    // =========================================================================
    @Test
    @Order(8)
    @DisplayName("시나리오6: @CachePut - 상품 수정 시 캐시 갱신")
    void scenario6_CachePut() {
        // Given: 상품 조회해서 캐시에 저장
        Product original = scenario.findProduct(1L);
        assertEquals(1, scenario.getCallCount());

        // When: 상품 정보 수정
        Product updated = new Product(1L, "맥북 프로 M3", 4000000, 5, "PREMIUM");
        scenario.updateProduct(updated);

        // Then: 캐시에서 다시 조회하면 수정된 정보가 나와야 함
        scenario.resetCallCount();
        Product cached = scenario.findProduct(1L);

        assertEquals(0, scenario.getCallCount(),
                "캐시 히트해야 합니다");
        assertEquals("맥북 프로 M3", cached.getName(),
                "❌ 캐시된 정보가 갱신되어야 합니다. @CachePut(cacheNames=\"products\", key=\"#product.id\")를 사용했나요?");
    }

    // =========================================================================
    // 📌 시나리오 7: 캐시 삭제 (@CacheEvict)
    // =========================================================================
    @Test
    @Order(9)
    @DisplayName("시나리오7: @CacheEvict - 상품 삭제 시 캐시도 삭제")
    void scenario7_CacheEvict() {
        // Given: 상품 조회해서 캐시에 저장
        scenario.findProduct(1L);
        assertEquals(1, scenario.getCallCount());

        // 캐시 확인 (캐시 히트)
        scenario.findProduct(1L);
        assertEquals(1, scenario.getCallCount());  // 캐시 히트

        // When: 상품 삭제
        scenario.deleteProduct(1L);

        // Then: 캐시에서도 삭제되어야 함
        scenario.resetCallCount();
        scenario.addProduct(new Product(1L, "새상품", 10000, 10));  // 다시 추가
        scenario.findProduct(1L);

        assertEquals(1, scenario.getCallCount(),
                "❌ 캐시에서 삭제되어 다시 메서드가 실행되어야 합니다. @CacheEvict(\"products\")를 추가했나요?");
    }

    // =========================================================================
    // 📌 시나리오 8: 전체 캐시 삭제
    // =========================================================================
    @Test
    @Order(10)
    @DisplayName("시나리오8: @CacheEvict allEntries - 전체 캐시 삭제")
    void scenario8_CacheEvictAll() {
        // Given: 여러 상품 조회해서 캐시에 저장
        scenario.findProduct(1L);
        scenario.findProduct(2L);
        assertEquals(2, scenario.getCallCount());

        // 캐시 확인 (캐시 히트)
        scenario.findProduct(1L);
        scenario.findProduct(2L);
        assertEquals(2, scenario.getCallCount());  // 여전히 2

        // When: 전체 캐시 삭제
        scenario.clearProductCache();

        // Then: 모든 캐시가 삭제되어야 함
        scenario.resetCallCount();
        scenario.findProduct(1L);
        scenario.findProduct(2L);

        assertEquals(2, scenario.getCallCount(),
                "❌ 전체 캐시가 삭제되어야 합니다. @CacheEvict(cacheNames=\"products\", allEntries=true)를 사용했나요?");
    }

    // =========================================================================
    // 📌 시나리오 9: 커스텀 KeyGenerator
    // =========================================================================
    @Test
    @Order(11)
    @DisplayName("시나리오9: keyGenerator - 커스텀 키 형식 확인")
    void scenario9_CustomKeyGenerator() {
        // When: 커스텀 키 생성기로 조회
        scenario.findProductWithCustomKey(1L);

        // Then: "클래스명.메서드명:[파라미터]" 형식의 키로 캐시됨
        Cache cache = primaryCacheManager.getCache("products");
        assertNotNull(cache);

        String expectedKey = "ProductCacheScenario.findProductWithCustomKey:[1]";
        Cache.ValueWrapper cached = cache.get(expectedKey);

        assertNotNull(cached,
                "❌ 커스텀 키 형식으로 캐시되어야 합니다. keyGenerator=\"customKeyGenerator\"를 추가했나요?");
    }

    // =========================================================================
    // 📌 시나리오 10: CacheResolver
    // =========================================================================
    @Test
    @Order(12)
    @DisplayName("시나리오10: cacheResolver - VIP는 별도 캐시 사용")
    void scenario10_CacheResolver() {
        // When: VIP 고객 조회 후 같은 요청 반복
        scenario.findProductForGrade("VIP", 1L);
        scenario.findProductForGrade("VIP", 1L);  // 캐시 히트 (vipBooks 캐시)
        int countAfterVip = scenario.getCallCount();

        scenario.resetCallCount();

        // When: 일반 고객 조회 후 같은 요청 반복
        scenario.findProductForGrade("STANDARD", 2L);
        scenario.findProductForGrade("STANDARD", 2L);  // 캐시 히트 (advancedBooks 캐시)
        int countAfterStandard = scenario.getCallCount();

        // Then: 각각 1번씩만 실행 (캐시 히트)
        assertEquals(1, countAfterVip,
                "❌ VIP 요청이 vipBooks 캐시에 저장되어야 합니다. cacheResolver=\"runtimeCacheResolver\"를 사용했나요?");
        assertEquals(1, countAfterStandard,
                "❌ 일반 고객 요청이 advancedBooks 캐시에 저장되어야 합니다");
    }

    // =========================================================================
    // 📌 시나리오 11: 특정 CacheManager
    // =========================================================================
    @Test
    @Order(13)
    @DisplayName("시나리오11: cacheManager - 프리미엄 전용 캐시 매니저")
    void scenario11_SpecificCacheManager() {
        // When: 프리미엄 상품 조회
        scenario.findPremiumProduct(1L);

        // Then: vipCacheManager의 vipBooks 캐시에 저장됨
        Cache vipCache = vipCacheManager.getCache("vipBooks");
        assertNotNull(vipCache.get(1L),
                "❌ vipCacheManager를 사용해야 합니다. cacheManager=\"vipCacheManager\"를 추가했나요?");
    }

    // =========================================================================
    // 📌 시나리오 12: 종합
    // =========================================================================
    @Test
    @Order(14)
    @DisplayName("시나리오12: 종합 - condition + unless + key 조합")
    void scenario12_Combined() {
        // Given:
        // - 상품ID > 0 (condition)
        // - 결과가 null이 아니고 품절이 아님 (unless)
        // - 키: "고객ID_상품ID"

        // When: 조건을 모두 만족하는 경우
        scenario.findFilteredProduct(1L, 2L);  // 키보드, 재고 있음
        scenario.findFilteredProduct(1L, 2L);
        int countAfterValid = scenario.getCallCount();

        scenario.resetCallCount();

        // When: 품절 상품 (unless 조건)
        scenario.findFilteredProduct(1L, 3L);  // 마우스, 품절
        scenario.findFilteredProduct(1L, 3L);
        int countAfterSoldOut = scenario.getCallCount();

        // Then:
        assertEquals(1, countAfterValid,
                "❌ 조건을 만족하면 캐시되어야 합니다");
        assertEquals(2, countAfterSoldOut,
                "❌ 품절 상품은 unless 조건으로 캐시에서 제외되어야 합니다");
    }

    // =========================================================================
    // 💡 도움말
    // =========================================================================
    /*
     * 테스트가 실패하면 에러 메시지의 힌트를 확인하세요!
     *
     * 정답은 src/main/java/.../scenario/answer/ProductCacheScenarioAnswer.java
     * 에서 확인할 수 있습니다.
     *
     * 주요 어노테이션 패턴:
     *
     * 1. 기본 캐싱:
     *    @Cacheable("캐시이름")
     *
     * 2. 조건부 캐싱:
     *    @Cacheable(cacheNames="...", condition="#price > 10000")
     *
     * 3. 결과 기반 제외:
     *    @Cacheable(cacheNames="...", unless="#result == null")
     *
     * 4. 커스텀 키:
     *    @Cacheable(cacheNames="...", key="#param1 + '_' + #param2")
     *
     * 5. 동시성:
     *    @Cacheable(cacheNames="...", sync=true)
     *
     * 6. 캐시 갱신:
     *    @CachePut(cacheNames="...", key="#obj.id")
     *
     * 7. 캐시 삭제:
     *    @CacheEvict(cacheNames="...", key="#id")
     *    @CacheEvict(cacheNames="...", allEntries=true)
     *
     * 8. KeyGenerator:
     *    @Cacheable(cacheNames="...", keyGenerator="beanName")
     *
     * 9. CacheResolver:
     *    @Cacheable(cacheResolver="beanName")
     *
     * 10. CacheManager:
     *     @Cacheable(cacheNames="...", cacheManager="beanName")
     */
}
