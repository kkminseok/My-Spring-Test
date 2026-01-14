package org.my.springcachetest.scenario.answer;

import org.my.springcachetest.domain.Customer;
import org.my.springcachetest.domain.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 🎯 Spring Cache 시나리오 실습 - 정답
 * =============================================================================
 *
 * 각 메서드의 어노테이션이 정답입니다.
 * 자신의 답과 비교해보세요!
 */
@Service("productCacheScenarioAnswer")  // 빈 이름 충돌 방지
public class ProductCacheScenarioAnswer {

    private final Map<Long, Product> productStore = new HashMap<>();
    private final Map<Long, Customer> customerStore = new HashMap<>();
    private final AtomicInteger callCount = new AtomicInteger(0);

    public ProductCacheScenarioAnswer() {
        productStore.put(1L, new Product(1L, "맥북 프로", 3500000, 10, "PREMIUM"));
        productStore.put(2L, new Product(2L, "무선 키보드", 89000, 50, "STANDARD"));
        productStore.put(3L, new Product(3L, "게이밍 마우스", 45000, 0, "STANDARD"));
        productStore.put(4L, new Product(4L, "4K 모니터", 800000, 5, "PREMIUM"));
        productStore.put(5L, new Product(5L, "USB 허브", 15000, 100, "STANDARD"));

        customerStore.put(1L, new Customer(1L, "김VIP", "VIP", 10000000));
        customerStore.put(2L, new Customer(2L, "이골드", "GOLD", 5000000));
        customerStore.put(3L, new Customer(3L, "박일반", "STANDARD", 100000));
    }

    // =========================================================================
    // 📌 시나리오 1: 기본 캐싱 - 정답
    // =========================================================================
    /**
     * ✅ 정답: @Cacheable("products")
     *
     * 💡 설명:
     * - cacheNames (또는 value)에 캐시 이름 지정
     * - 파라미터 productId가 자동으로 캐시 키가 됨
     */
    @Cacheable("products")
    public Product findProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 2: 조건부 캐싱 (condition) - 정답
    // =========================================================================
    /**
     * ✅ 정답: condition으로 가격 조건 체크
     *
     * 💡 설명:
     * - condition은 메서드 실행 전에 평가됨
     * - 파라미터(#productId)는 사용 가능
     * - 결과값(#result)은 condition에서 사용 불가!
     *
     * ⚠️ 주의:
     * - condition에서는 #result를 사용할 수 없습니다
     * - 가격 조건은 파라미터로 전달받거나 unless를 사용해야 합니다
     * - 이 예제에서는 productId를 기준으로 조건을 검
     *
     * 💡 실무 팁:
     * - ID 기반으로 고가 상품 여부를 판단하거나
     * - 별도 파라미터로 가격을 전달하거나
     * - unless를 사용하여 결과 기반으로 필터링
     */
    @Cacheable(cacheNames = "expensiveProducts",
               condition = "#productId != null && #productId <= 4")  // ID 1~4만 캐시 (예시)
    public Product findExpensiveProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 3: 결과 기반 제외 (unless) - 정답
    // =========================================================================
    /**
     * ✅ 정답: unless로 품절/null 제외
     *
     * 💡 설명:
     * - unless는 메서드 실행 후 결과를 보고 판단
     * - #result로 반환값에 접근 가능
     * - unless가 true면 캐시에 저장하지 않음
     */
    @Cacheable(cacheNames = "availableProducts",
               unless = "#result == null || #result.soldOut")
    public Product findAvailableProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 4: 복합 키 생성 - 정답
    // =========================================================================
    /**
     * ✅ 정답: SpEL로 여러 파라미터 조합
     *
     * 💡 설명:
     * - key 속성에 SpEL 표현식 사용
     * - 문자열 연결: #param1 + '_' + #param2
     * - 결과: "1_2" 형태의 키
     */
    @Cacheable(cacheNames = "customerProducts",
               key = "#customerId + '_' + #productId")
    public Product findProductForCustomer(Long customerId, Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 5: 동시성 제어 (sync) - 정답
    // =========================================================================
    /**
     * ✅ 정답: sync = true
     *
     * 💡 설명:
     * - sync=true: 동시 요청 시 하나만 메서드 실행
     * - 나머지는 캐시 저장될 때까지 대기
     * - Cache Stampede 방지!
     *
     * ⚠️ 주의:
     * - sync=true는 condition, unless와 함께 사용 불가
     */
    @Cacheable(cacheNames = "popularProducts", sync = true)
    public Product findPopularProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 6: 캐시 갱신 (@CachePut) - 정답
    // =========================================================================
    /**
     * ✅ 정답: @CachePut으로 캐시 갱신
     *
     * 💡 설명:
     * - @CachePut은 항상 메서드를 실행함
     * - 실행 결과를 캐시에 저장
     * - key="#product.id": 파라미터 객체의 속성을 키로 사용
     */
    @CachePut(cacheNames = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        callCount.incrementAndGet();
        productStore.put(product.getId(), product);
        return product;
    }

    // =========================================================================
    // 📌 시나리오 7: 캐시 삭제 (@CacheEvict) - 정답
    // =========================================================================
    /**
     * ✅ 정답: @CacheEvict로 캐시 삭제
     *
     * 💡 설명:
     * - 지정한 캐시에서 해당 키의 엔트리 삭제
     * - key를 명시하지 않으면 파라미터가 키가 됨
     */
    @CacheEvict(cacheNames = "products")
    public void deleteProduct(Long productId) {
        callCount.incrementAndGet();
        productStore.remove(productId);
    }

    // =========================================================================
    // 📌 시나리오 8: 전체 캐시 삭제 - 정답
    // =========================================================================
    /**
     * ✅ 정답: allEntries = true
     *
     * 💡 설명:
     * - allEntries=true: 캐시의 모든 엔트리 삭제
     * - 대량 데이터 변경 후 유용
     */
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void clearProductCache() {
        callCount.incrementAndGet();
    }

    // =========================================================================
    // 📌 시나리오 9: 커스텀 KeyGenerator - 정답
    // =========================================================================
    /**
     * ✅ 정답: keyGenerator 속성 사용
     *
     * 💡 설명:
     * - 미리 등록된 KeyGenerator 빈을 사용
     * - "클래스명.메서드명:[파라미터]" 형식의 키 생성
     */
    @Cacheable(cacheNames = "products", keyGenerator = "customKeyGenerator")
    public Product findProductWithCustomKey(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 10: CacheResolver - 정답
    // =========================================================================
    /**
     * ✅ 정답: cacheResolver 속성 사용
     *
     * 💡 설명:
     * - 런타임에 어떤 캐시를 사용할지 결정
     * - runtimeCacheResolver: "VIP" 포함 여부로 캐시 선택
     *
     * ⚠️ 주의:
     * - cacheResolver와 cacheNames를 함께 사용하면 cacheResolver가 우선
     */
    @Cacheable(cacheResolver = "runtimeCacheResolver")
    public Product findProductForGrade(String customerGrade, Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 11: 특정 CacheManager - 정답
    // =========================================================================
    /**
     * ✅ 정답: cacheManager 속성 사용
     *
     * 💡 설명:
     * - 특정 CacheManager를 지정하여 사용
     * - @Primary가 아닌 다른 매니저 사용 시 명시 필요
     */
    @Cacheable(cacheNames = "vipBooks", cacheManager = "vipCacheManager")
    public Product findPremiumProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 12: 종합 - 정답
    // =========================================================================
    /**
     * ✅ 정답: condition + unless + key 조합
     *
     * 💡 설명:
     * - condition: 메서드 실행 전 조건 (파라미터 기반)
     * - unless: 메서드 실행 후 조건 (결과 기반)
     * - key: 복합 키 생성
     */
    @Cacheable(cacheNames = "filteredProducts",
               condition = "#productId > 0",
               unless = "#result == null || #result.soldOut",
               key = "#customerId + '_' + #productId")
    public Product findFilteredProduct(Long customerId, Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 유틸리티 메서드
    // =========================================================================
    private void simulateDbCall() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCallCount() {
        return callCount.get();
    }

    public void resetCallCount() {
        callCount.set(0);
    }

    public Product getProductDirectly(Long id) {
        return productStore.get(id);
    }

    public void addProduct(Product product) {
        productStore.put(product.getId(), product);
    }

    public Customer getCustomer(Long id) {
        return customerStore.get(id);
    }
}
