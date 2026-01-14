package org.my.springcachetest.service;

import org.my.springcachetest.domain.Book;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * 📚 Spring Cache 학습용 BookService
 * =============================================================================
 *
 * 이 서비스는 Spring Cache의 다양한 기능을 테스트하기 위한 예제입니다.
 * 각 메서드의 어노테이션을 확인하고, 테스트 코드에서 빈칸을 채워보세요!
 */
@Service
public class BookService {

    // 인메모리 데이터 저장소 (DB 대신 사용)
    private final Map<String, Book> bookStore = new HashMap<>();

    // 메서드 호출 횟수 추적용 카운터
    private final AtomicInteger findByIsbnCallCount = new AtomicInteger(0);
    private final AtomicInteger findByTitleCallCount = new AtomicInteger(0);
    private final AtomicInteger updateBookCallCount = new AtomicInteger(0);
    private final AtomicInteger deleteBookCallCount = new AtomicInteger(0);

    public BookService() {
        // 초기 데이터 설정
        bookStore.put("978-0-13-468599-1", new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 45000));
        bookStore.put("978-0-596-51774-8", new Book("978-0-596-51774-8", "JavaScript: The Good Parts", "Douglas Crockford", 25000));
        bookStore.put("978-1-61729-800-0", new Book("978-1-61729-800-0", "Spring in Action", "Craig Walls", 55000, true)); // 양장본
    }

    // =========================================================================
    // 📌 1. @Cacheable 기본 사용
    // =========================================================================
    /**
     * 가장 기본적인 @Cacheable 사용법
     * - 캐시 이름: "books"
     * - 키: 메서드 파라미터(isbn)가 자동으로 키가 됨
     *
     * 💡 학습 포인트:
     * - 같은 isbn으로 호출하면 메서드가 실행되지 않고 캐시된 값 반환
     * - callCount로 실제 메서드 호출 여부 확인 가능
     */
    @Cacheable("books")
    public Book findByIsbn(String isbn) {
        findByIsbnCallCount.incrementAndGet();
        simulateSlowService();  // DB 조회 시뮬레이션
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 2. @Cacheable - 커스텀 키 지정
    // =========================================================================
    /**
     * SpEL을 사용한 커스텀 키 지정
     * - key="#title": title 파라미터를 캐시 키로 사용
     *
     * 💡 학습 포인트:
     * - 여러 파라미터 중 특정 파라미터만 키로 사용 가능
     * - includeOutOfPrint 값이 달라도 title이 같으면 캐시 히트
     */
    @Cacheable(cacheNames = "books", key = "#title")
    public Book findByTitle(String title, boolean includeOutOfPrint) {
        findByTitleCallCount.incrementAndGet();
        simulateSlowService();
        return bookStore.values().stream()
                .filter(book -> book.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    // =========================================================================
    // 📌 3. @Cacheable - condition 조건부 캐싱
    // =========================================================================
    /**
     * condition을 사용한 조건부 캐싱
     * - condition="#isbn.length() > 10": ISBN 길이가 10 초과일 때만 캐싱
     *
     * 💡 학습 포인트:
     * - condition이 false면 캐시를 아예 사용하지 않음 (저장도, 조회도 안함)
     */
    @Cacheable(cacheNames = "books", condition = "#isbn.length() > 10")
    public Book findByIsbnWithCondition(String isbn) {
        findByIsbnCallCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 4. @Cacheable - unless 결과 기반 제외
    // =========================================================================
    /**
     * unless를 사용한 결과 기반 캐싱 제외
     * - unless="#result == null": 결과가 null이면 캐시에 저장하지 않음
     * - unless="#result.hardback": 양장본이면 캐시에 저장하지 않음
     *
     * 💡 학습 포인트:
     * - condition과 달리 unless는 메서드 실행 후 결과를 보고 캐싱 여부 결정
     * - #result로 반환값에 접근 가능
     */
    @Cacheable(cacheNames = "booksNoHardback", unless = "#result == null || #result.hardback")
    public Book findByIsbnExcludeHardback(String isbn) {
        findByIsbnCallCount.incrementAndGet();
        simulateSlowService();
        return bookStore.get(isbn);
    }

    // =========================================================================
    // 📌 5. @CachePut - 캐시 업데이트
    // =========================================================================
    /**
     * @CachePut은 메서드를 항상 실행하고 결과를 캐시에 저장
     * - @Cacheable과 달리 캐시 존재 여부와 관계없이 메서드 실행
     *
     * 💡 학습 포인트:
     * - 데이터 업데이트 시 캐시도 함께 갱신할 때 사용
     * - key="#book.isbn": 파라미터 객체의 속성을 키로 사용
     */
    @CachePut(cacheNames = "books", key = "#book.isbn")
    public Book updateBook(Book book) {
        updateBookCallCount.incrementAndGet();
        bookStore.put(book.getIsbn(), book);
        return book;
    }

    // =========================================================================
    // 📌 6. @CacheEvict - 캐시 삭제
    // =========================================================================
    /**
     * 특정 키의 캐시 엔트리 삭제
     *
     * 💡 학습 포인트:
     * - 데이터 삭제 시 해당 캐시도 함께 삭제
     */
    @CacheEvict(cacheNames = "books", key = "#isbn")
    public void deleteBook(String isbn) {
        deleteBookCallCount.incrementAndGet();
        bookStore.remove(isbn);
    }

    // =========================================================================
    // 📌 7. @CacheEvict - allEntries로 전체 삭제
    // =========================================================================
    /**
     * allEntries=true로 캐시의 모든 엔트리 삭제
     *
     * 💡 학습 포인트:
     * - 대량 데이터 변경 후 캐시 전체를 무효화할 때 사용
     * - 개별 키 삭제보다 효율적일 수 있음
     */
    @CacheEvict(cacheNames = "books", allEntries = true)
    public void clearAllBooks() {
        bookStore.clear();
    }

    // =========================================================================
    // 📌 8. @CacheEvict - beforeInvocation
    // =========================================================================
    /**
     * beforeInvocation=true: 메서드 실행 전에 캐시 삭제
     *
     * 💡 학습 포인트:
     * - 기본값(false)은 메서드 성공 후 캐시 삭제
     * - true면 메서드 실행 전에 삭제 (예외 발생해도 삭제됨)
     */
    @CacheEvict(cacheNames = "books", beforeInvocation = true)
    public void deleteBookBeforeInvocation(String isbn) {
        deleteBookCallCount.incrementAndGet();
        // 예외가 발생해도 캐시는 이미 삭제된 상태
        if (isbn.startsWith("INVALID")) {
            throw new IllegalArgumentException("Invalid ISBN");
        }
        bookStore.remove(isbn);
    }

    // =========================================================================
    // 📌 9. @Caching - 다중 캐시 작업
    // =========================================================================
    /**
     * 여러 캐시 작업을 한 메서드에서 수행
     *
     * 💡 학습 포인트:
     * - 같은 타입의 어노테이션 여러 개 적용 가능
     * - 서로 다른 타입의 어노테이션도 조합 가능
     */
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "books", key = "#isbn"),
                    @CacheEvict(cacheNames = "booksNoHardback", key = "#isbn")
            }
    )
    public void deleteBookFromAllCaches(String isbn) {
        deleteBookCallCount.incrementAndGet();
        bookStore.remove(isbn);
    }

    // =========================================================================
    // 유틸리티 메서드
    // =========================================================================
    private void simulateSlowService() {
        try {
            Thread.sleep(100);  // DB 조회 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFindByIsbnCallCount() {
        return findByIsbnCallCount.get();
    }

    public int getFindByTitleCallCount() {
        return findByTitleCallCount.get();
    }

    public int getUpdateBookCallCount() {
        return updateBookCallCount.get();
    }

    public int getDeleteBookCallCount() {
        return deleteBookCallCount.get();
    }

    public void resetCallCounts() {
        findByIsbnCallCount.set(0);
        findByTitleCallCount.set(0);
        updateBookCallCount.set(0);
        deleteBookCallCount.set(0);
    }

    public void resetBookStore() {
        bookStore.clear();
        bookStore.put("978-0-13-468599-1", new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 45000));
        bookStore.put("978-0-596-51774-8", new Book("978-0-596-51774-8", "JavaScript: The Good Parts", "Douglas Crockford", 25000));
        bookStore.put("978-1-61729-800-0", new Book("978-1-61729-800-0", "Spring in Action", "Craig Walls", 55000, true));
    }

    public Book getBookDirectly(String isbn) {
        return bookStore.get(isbn);
    }

    public void addBook(Book book) {
        bookStore.put(book.getIsbn(), book);
    }
}
