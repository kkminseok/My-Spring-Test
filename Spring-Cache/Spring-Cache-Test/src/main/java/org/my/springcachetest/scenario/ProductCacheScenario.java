package org.my.springcachetest.scenario;

import org.my.springcachetest.domain.Customer;
import org.my.springcachetest.domain.Product;
// TODO: 필요한 캐시 어노테이션을 import 하세요
// import org.springframework.cache.annotation.CacheEvict;
// import org.springframework.cache.annotation.CachePut;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 🎯 Spring Cache 시나리오 실습
 * =============================================================================
 *
 * 각 메서드에 적절한 캐시 어노테이션을 직접 작성해보세요!
 * 시나리오를 읽고, 요구사항에 맞는 어노테이션과 속성을 적용하면 됩니다.
 *
 * 💡 사용 가능한 어노테이션:
 * - @Cacheable: 캐시 조회/저장
 * - @CachePut: 캐시 갱신 (항상 메서드 실행)
 * - @CacheEvict: 캐시 삭제
 *
 * 💡 주요 속성들:
 * - cacheNames / value: 캐시 이름
 * - key: 캐시 키 (SpEL)
 * - condition: 캐싱 조건 (true일 때만 캐시)
 * - unless: 캐싱 제외 조건 (true이면 캐시 안 함)
 * - sync: 동시성 제어
 * - keyGenerator: 커스텀 키 생성기
 * - cacheManager: 사용할 캐시 매니저
 * - cacheResolver: 런타임 캐시 결정
 *
 * 🧪 테스트 실행:
 * ./gradlew test --tests "*.ScenarioTest"
 *
 * 📝 정답 확인:
 * src/main/java/.../scenario/answer/ProductCacheScenarioAnswer.java
 */
@Service
public class ProductCacheScenario {

    private final Map<Long, Product> productStore = new HashMap<>();
    private final Map<Long, Customer> customerStore = new HashMap<>();
    private final AtomicInteger callCount = new AtomicInteger(0);

    public ProductCacheScenario() {
        // 상품 데이터
        productStore.put(1L, new Product(1L, "맥북 프로", 3500000, 10, "PREMIUM"));
        productStore.put(2L, new Product(2L, "무선 키보드", 89000, 50, "STANDARD"));
        productStore.put(3L, new Product(3L, "게이밍 마우스", 45000, 0, "STANDARD"));  // 품절!
        productStore.put(4L, new Product(4L, "4K 모니터", 800000, 5, "PREMIUM"));
        productStore.put(5L, new Product(5L, "USB 허브", 15000, 100, "STANDARD"));

        // 고객 데이터
        customerStore.put(1L, new Customer(1L, "김VIP", "VIP", 10000000));
        customerStore.put(2L, new Customer(2L, "이골드", "GOLD", 5000000));
        customerStore.put(3L, new Customer(3L, "박일반", "STANDARD", 100000));
    }

    // =========================================================================
    // 📌 시나리오 1: 기본 캐싱
    // =========================================================================
    /**
     * 🎯 시나리오: 상품 조회 캐싱
     *
     * 요구사항:
     * - 상품 ID로 조회한 결과를 "products" 캐시에 저장하세요
     * - 같은 ID로 재조회 시 캐시된 결과를 반환해야 합니다
     *
     * 힌트: 가장 기본적인 @Cacheable 사용
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products")
    public Product findProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 2: 조건부 캐싱 (condition)
    // =========================================================================
    /**
     * 🎯 시나리오: 특정 조건의 상품만 캐싱
     *
     * 요구사항:
     * - "expensiveProducts" 캐시 사용
     * - productId가 4 이하인 경우만 캐시 (ID 1~4가 고가 상품이라 가정)
     * - ID 5 이상은 캐시하지 않음
     *
     * 힌트: condition 속성을 사용하세요
     *
     * ⚠️ 주의: condition은 메서드 실행 전에 평가됩니다!
     *         파라미터(#productId)는 사용 가능하지만,
     *         결과값(#result)은 사용할 수 없습니다.
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", condition = "#productId == 1L")
    public Product findExpensiveProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 3: 결과 기반 제외 (unless)
    // =========================================================================
    /**
     * 🎯 시나리오: 품절 상품 캐시 제외
     *
     * 요구사항:
     * - "availableProducts" 캐시 사용
     * - 조회 결과가 품절(soldOut=true)이면 캐시에 저장하지 마세요
     * - 품절 상품은 재고가 들어올 수 있으므로 캐시하면 안 됩니다
     * - null 결과도 캐시하지 마세요
     *
     * 힌트: unless 속성을 사용하세요
     * unless는 메서드 실행 후 결과를 보고 캐싱 여부를 결정합니다
     * #result로 반환값에 접근할 수 있습니다
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", unless = "#result == null || #result.stock == 0")
    public Product findAvailableProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 4: 복합 키 생성
    // =========================================================================
    /**
     * 🎯 시나리오: 고객별 상품 조회 캐싱
     *
     * 요구사항:
     * - "customerProducts" 캐시 사용
     * - 같은 상품이라도 고객마다 다른 가격/정보를 보여줄 수 있음
     * - 캐시 키를 "고객ID_상품ID" 형태로 만드세요
     *   예: 고객1이 상품2 조회 -> 키는 "1_2"
     *
     * 힌트: key 속성에서 SpEL로 여러 파라미터를 조합하세요
     * 문자열 연결: #param1 + '_' + #param2
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", key = "#p0 + '_' + #p1")
    public Product findProductForCustomer(Long customerId, Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 5: 동시성 제어 (sync)
    // =========================================================================
    /**
     * 🎯 시나리오: 인기 상품 조회 - Cache Stampede 방지
     *
     * 요구사항:
     * - "popularProducts" 캐시 사용
     * - 인기 상품은 동시에 많은 요청이 들어올 수 있음
     * - 캐시 미스 시 하나의 요청만 DB를 조회하고,
     *   나머지 요청은 그 결과를 기다려야 합니다
     *
     * 힌트: sync 속성을 사용하세요
     *
     * ⚠️ Cache Stampede란?
     * 캐시가 없을 때 동시에 100개 요청이 들어오면
     * 100개 모두 DB를 조회하여 DB에 과부하가 걸리는 현상
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", sync = true)
    public Product findPopularProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 6: 캐시 갱신 (@CachePut)
    // =========================================================================
    /**
     * 🎯 시나리오: 상품 정보 수정 시 캐시 갱신
     *
     * 요구사항:
     * - "products" 캐시 갱신
     * - 상품 정보를 수정하면 캐시도 최신 정보로 업데이트되어야 함
     * - 키는 상품의 ID (product.getId())
     *
     * 힌트: @CachePut은 항상 메서드를 실행하고 결과를 캐시에 저장합니다
     * @Cacheable과 달리 캐시 존재 여부와 관계없이 실행됩니다
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @CachePut(cacheNames = "products", key="#product.id")
    public Product updateProduct(Product product) {
        callCount.incrementAndGet();
        productStore.put(product.getId(), product);
        return product;
    }

    // =========================================================================
    // 📌 시나리오 7: 캐시 삭제 (@CacheEvict)
    // =========================================================================
    /**
     * 🎯 시나리오: 상품 삭제 시 캐시도 삭제
     *
     * 요구사항:
     * - "products" 캐시에서 해당 상품 삭제
     * - 상품이 삭제되면 캐시에도 남아있으면 안 됩니다
     *
     * 힌트: @CacheEvict를 사용하세요
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @CacheEvict(cacheNames = "products")
    public void deleteProduct(Long productId) {
        callCount.incrementAndGet();
        productStore.remove(productId);
    }

    // =========================================================================
    // 📌 시나리오 8: 전체 캐시 삭제
    // =========================================================================
    /**
     * 🎯 시나리오: 상품 대량 업데이트 후 캐시 전체 초기화
     *
     * 요구사항:
     * - "products" 캐시의 모든 항목 삭제
     * - 대량 업데이트 시 개별 삭제보다 전체 삭제가 효율적
     *
     * 힌트: @CacheEvict의 allEntries 속성을 사용하세요
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void clearProductCache() {
        callCount.incrementAndGet();
        // 캐시만 삭제하고 데이터는 유지
    }

    // =========================================================================
    // 📌 시나리오 9: 커스텀 KeyGenerator 사용
    // =========================================================================
    /**
     * 🎯 시나리오: 메서드명을 포함한 캐시 키 생성
     *
     * 요구사항:
     * - "products" 캐시 사용
     * - 키 형식: "클래스명.메서드명:[파라미터]"
     * - 이미 등록된 "customKeyGenerator" 빈을 사용하세요
     *
     * 힌트: keyGenerator 속성을 사용하세요
     *
     * 💡 왜 필요한가?
     * - 서로 다른 메서드가 같은 파라미터를 사용할 때 키 충돌 방지
     * - 디버깅 시 어떤 메서드의 캐시인지 파악 용이
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", keyGenerator = "customKeyGenerator")
    public Product findProductWithCustomKey(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 10: CacheResolver로 동적 캐시 선택
    // =========================================================================
    /**
     * 🎯 시나리오: VIP 고객은 별도 캐시 사용
     *
     * 요구사항:
     * - VIP 고객의 조회는 "vipBooks" 캐시 사용 (vipCacheManager)
     * - 일반 고객의 조회는 "advancedBooks" 캐시 사용 (primaryCacheManager)
     * - 이미 등록된 "runtimeCacheResolver" 빈을 사용하세요
     *
     * 힌트: cacheResolver 속성을 사용하세요
     *
     * ⚠️ runtimeCacheResolver의 동작:
     * - 파라미터에 "VIP"가 포함되면 vipBooks 캐시 사용
     * - 그 외에는 advancedBooks 캐시 사용
     *
     * 💡 왜 필요한가?
     * - VIP 고객 데이터는 더 오래 캐시
     * - 또는 VIP 캐시는 더 큰 저장소 사용
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheResolver = "runtimeCacheResolver")
    public Product findProductForGrade(String customerGrade, Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 11: 특정 CacheManager 지정
    // =========================================================================
    /**
     * 🎯 시나리오: 프리미엄 상품 전용 캐시
     *
     * 요구사항:
     * - "vipBooks" 캐시 사용
     * - "vipCacheManager" 캐시 매니저 사용 (프리미엄 전용)
     *
     * 힌트: cacheManager 속성을 사용하세요
     *
     * 💡 왜 필요한가?
     * - 프리미엄 상품은 별도의 캐시 정책 적용
     * - 예: 더 긴 TTL, 더 큰 저장 공간
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "vipBooks", cacheManager = "vipCacheManager")
    public Product findPremiumProduct(Long productId) {
        callCount.incrementAndGet();
        simulateDbCall();
        return productStore.get(productId);
    }

    // =========================================================================
    // 📌 시나리오 12: 종합 - 조건 + 제외 + 커스텀 키
    // =========================================================================
    /**
     * 🎯 시나리오: 복합 조건의 캐싱
     *
     * 요구사항:
     * - "filteredProducts" 캐시 사용
     * - 조건 1: 상품 ID가 0보다 커야 함 (condition)
     * - 조건 2: 결과가 null이거나 품절이면 캐시 안 함 (unless)
     * - 키: "고객ID_상품ID" 형태
     *
     * 힌트: condition, unless, key를 모두 조합하세요
     */
    // TODO: 여기에 캐시 어노테이션을 작성하세요
    @Cacheable(cacheNames = "products", key = "#customerId + '_' + #productId",
            condition = "#productId >0",
            unless = "#result == null || #result.soldOut")
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
            Thread.sleep(100);  // DB 조회 시뮬레이션
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
