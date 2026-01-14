package org.my.springcachetest.service;

import org.my.springcachetest.domain.Book;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 🔬 Spring Cache 심화 기능 학습용 서비스
 * =============================================================================
 *
 * 이 서비스에서 학습할 수 있는 심화 기능:
 *
 * 1. keyGenerator - 커스텀 키 생성기 사용
 * 2. cacheResolver - 런타임 캐시 결정
 * 3. cacheManager - 특정 캐시 매니저 지정
 * 4. sync - 동시성 제어 (Cache Stampede 방지)
 * 5. @CacheConfig - 클래스 레벨 캐시 설정
 *
 * 💡 각 메서드의 어노테이션을 확인하고, 동작 방식을 이해해보세요!
 */
@Service
@CacheConfig(cacheNames = "advancedBooks")  // 클래스 레벨 기본 캐시 설정
public class AdvancedBookService {

    private final Map<String, Book> bookStore = new HashMap<>();
    private final AtomicInteger callCount = new AtomicInteger(0);
    private final AtomicInteger syncCallCount = new AtomicInteger(0);

    public AdvancedBookService() {
        // 초기 데이터
        bookStore.put("978-0-13-468599-1", new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 45000));
        bookStore.put("978-0-596-51774-8", new Book("978-0-596-51774-8", "JavaScript: The Good Parts", "Douglas Crockford", 25000));
        bookStore.put("978-1-61729-800-0", new Book("978-1-61729-800-0", "Spring in Action", "Craig Walls", 55000));
        bookStore.put("VIP-001", new Book("VIP-001", "VIP Exclusive Book", "VIP Author", 100000));
    }

    // =========================================================================
    // 📌 1. keyGenerator 사용
    // =========================================================================

    /**
     * 커스텀 KeyGenerator 사용
     * - keyGenerator = "customKeyGenerator": 클래스명.메서드명:[params] 형식의 키 생성
     *
     * 💡 학습 포인트:
     * - 기본 SimpleKeyGenerator는 파라미터만으로 키 생성
     * - customKeyGenerator는 "AdvancedBookService.findWithCustomKey:[isbn]" 형식
     * - 서로 다른 메서드가 같은 파라미터로 호출되어도 키가 다름
     *
     * ⚠️ 사용 시나리오:
     * - 여러 서비스에서 같은 파라미터로 캐시할 때 충돌 방지
     * - 디버깅 시 어떤 메서드의 캐시인지 파악 용이
     */
    @Cacheable(keyGenerator = "customKeyGenerator")
    public Book findWithCustomKeyGenerator(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    /**
     * ISBN 전용 KeyGenerator 사용
     * - ISBN의 체크섬(마지막 문자)을 제외한 키 생성
     *
     * 💡 학습 포인트:
     * - "978-0-13-468599-1"과 "978-0-13-468599-X"가 같은 키로 처리됨
     * - 비즈니스 로직에 맞는 키 생성 전략 적용 가능
     */
    @Cacheable(cacheNames = "books", keyGenerator = "isbnKeyGenerator")
    public Book findWithIsbnKeyGenerator(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    /**
     * 복합 키 생성기 사용
     * - 두 파라미터를 "param1_param2" 형식으로 조합
     */
    @Cacheable(cacheNames = "books", keyGenerator = "compositeKeyGenerator")
    public Book findWithCompositeKey(String title, String author) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.values().stream()
                .filter(b -> b.getTitle().equals(title) && b.getAuthor().equals(author))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // 📌 2. cacheResolver 사용
    // =========================================================================

    /**
     * 런타임 CacheResolver 사용
     * - cacheResolver = "runtimeCacheResolver": 파라미터에 따라 다른 캐시 선택
     *
     * 💡 학습 포인트:
     * - identifier에 "VIP"가 포함되면 vipBooks 캐시 사용
     * - 그 외에는 advancedBooks 캐시 사용
     * - cacheNames와 달리 런타임에 동적으로 캐시 결정
     *
     * ⚠️ 주의:
     * - cacheResolver와 cacheNames를 함께 사용하면 cacheResolver가 우선
     */
    @Cacheable(cacheResolver = "runtimeCacheResolver")
    public Book findWithRuntimeCacheResolver(String identifier) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(identifier);
    }

    // =========================================================================
    // 📌 3. cacheManager 지정
    // =========================================================================

    /**
     * 특정 CacheManager 사용
     * - cacheManager = "vipCacheManager": VIP 전용 캐시 매니저 사용
     *
     * 💡 학습 포인트:
     * - 여러 CacheManager가 있을 때 특정 매니저 지정
     * - @Primary로 지정된 기본 매니저 대신 다른 매니저 사용
     *
     * ⚠️ 사용 시나리오:
     * - VIP 고객 데이터는 별도 캐시 저장소에 저장
     * - 서로 다른 TTL, 크기 제한 적용
     */
    @Cacheable(cacheNames = "vipBooks", cacheManager = "vipCacheManager")
    public Book findVipBook(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    /**
     * 기본 CacheManager 사용 (비교용)
     * - cacheManager를 지정하지 않으면 @Primary 매니저 사용
     */
    @Cacheable(cacheNames = "books")
    public Book findNormalBook(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 4. sync 옵션 - 동시성 제어
    // =========================================================================

    /**
     * sync = true: 동시성 제어
     *
     * 💡 학습 포인트:
     * - 여러 스레드가 동시에 같은 키로 요청하면?
     *   - sync=false (기본): 모든 스레드가 메서드 실행 (Cache Stampede)
     *   - sync=true: 하나의 스레드만 실행, 나머지는 대기 후 캐시 값 사용
     *
     * ⚠️ Cache Stampede란?
     * - 캐시 미스 시 동시에 여러 요청이 DB를 조회하는 현상
     * - DB 부하 급증, 응답 지연 발생
     *
     * ⚠️ 주의사항:
     * - sync=true는 condition, unless와 함께 사용 불가
     * - 단일 캐시에만 적용 가능 (여러 캐시 X)
     */
    @Cacheable(cacheNames = "syncBooks", sync = true)
    public Book findWithSync(String isbn) {
        syncCallCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    /**
     * sync = false (기본값) - 비교용
     * - 동시 요청 시 모든 스레드가 메서드 실행
     */
    @Cacheable(cacheNames = "syncBooks", sync = false)
    public Book findWithoutSync(String isbn) {
        syncCallCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 5. @CacheConfig - 클래스 레벨 설정
    // =========================================================================

    /**
     * @CacheConfig에서 설정한 기본값 사용
     * - 클래스 레벨의 cacheNames = "advancedBooks" 적용
     *
     * 💡 학습 포인트:
     * - @CacheConfig로 클래스의 모든 메서드에 공통 설정 적용
     * - 메서드 레벨에서 개별 재정의 가능
     * - 설정 가능 항목: cacheNames, keyGenerator, cacheManager, cacheResolver
     */
    @Cacheable  // cacheNames 미지정 -> @CacheConfig의 "advancedBooks" 사용
    public Book findWithClassLevelConfig(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    /**
     * 메서드 레벨에서 @CacheConfig 재정의
     * - 클래스 설정보다 메서드 설정이 우선
     */
    @Cacheable(cacheNames = "books")  // @CacheConfig 재정의
    public Book findWithMethodLevelOverride(String isbn) {
        callCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 유틸리티 메서드
    // =========================================================================

    @CacheEvict(cacheNames = {"books", "advancedBooks", "syncBooks", "vipBooks"}, allEntries = true)
    public void clearAllCaches() {
        // 모든 캐시 클리어
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCallCount() {
        return callCount.get();
    }

    public int getSyncCallCount() {
        return syncCallCount.get();
    }

    public void resetCallCount() {
        callCount.set(0);
        syncCallCount.set(0);
    }

    public void addBook(String isbn, Book book) {
        bookStore.put(isbn, book);
    }

    public Book getBookDirectly(String isbn) {
        return bookStore.get(isbn);
    }
}
