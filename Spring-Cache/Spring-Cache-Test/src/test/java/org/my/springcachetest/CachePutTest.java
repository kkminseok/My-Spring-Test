package org.my.springcachetest;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Book;
import org.my.springcachetest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * 📝 @CachePut 학습 테스트
 * =============================================================================
 *
 * @CachePut vs @Cacheable:
 * - @Cacheable: 캐시에 있으면 메서드 실행 X, 캐시된 값 반환
 * - @CachePut: 항상 메서드 실행하고 결과를 캐시에 저장
 *
 * 💡 사용 케이스:
 * - 데이터 업데이트 시 캐시도 함께 갱신할 때
 * - 캐시에 최신 데이터를 강제로 저장할 때
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachePutTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    private static final String ISBN_EFFECTIVE_JAVA = "978-0-13-468599-1";

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        bookService.resetCallCounts();
        bookService.resetBookStore();
    }

    // =========================================================================
    // 📌 테스트 1: @CachePut은 항상 메서드 실행
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("@CachePut: 캐시 존재 여부와 관계없이 항상 메서드 실행")
    void test_CachePut_AlwaysExecutes() {
        // Given: 새 책 정보
        Book updatedBook = new Book(ISBN_EFFECTIVE_JAVA, "Effective Java 4th", "Joshua Bloch", 50000);

        // When: updateBook을 여러 번 호출
        bookService.updateBook(updatedBook);
        bookService.updateBook(updatedBook);
        bookService.updateBook(updatedBook);

        // Then: @CachePut은 캐시 여부와 관계없이 항상 메서드 실행
        // TODO: 빈칸을 채우세요
        assertEquals(_____, bookService.getUpdateBookCallCount(),
                "@CachePut은 매번 메서드를 실행합니다");
    }

    // =========================================================================
    // 📌 테스트 2: @CachePut으로 캐시 갱신
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("@CachePut: 캐시 데이터를 최신으로 갱신")
    void test_CachePut_UpdatesCache() {
        // Given: 먼저 캐시에 데이터 저장 (@Cacheable 통해)
        Book originalBook = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals("Effective Java", originalBook.getTitle());
        assertEquals(45000, originalBook.getPrice());

        // When: @CachePut으로 데이터 업데이트
        Book updatedBook = new Book(ISBN_EFFECTIVE_JAVA, "Effective Java 4th Edition", "Joshua Bloch", 55000);
        bookService.updateBook(updatedBook);

        // Then: 캐시에서 조회 시 업데이트된 데이터 반환
        bookService.resetCallCounts();
        Book cachedBook = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // TODO: 빈칸을 채우세요
        assertEquals(_____, cachedBook.getTitle(),
                "@CachePut이 캐시를 갱신했으므로 새 제목이 반환되어야 합니다");
        assertEquals(_____, cachedBook.getPrice(),
                "@CachePut이 캐시를 갱신했으므로 새 가격이 반환되어야 합니다");

        // 캐시 히트이므로 메서드 실행 안됨
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "캐시에서 조회했으므로 메서드 호출 횟수는?");
    }

    // =========================================================================
    // 📌 테스트 3: @CachePut vs @Cacheable 비교
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("@CachePut vs @Cacheable: 동작 방식 비교")
    void test_CachePut_vs_Cacheable() {
        // ===== @Cacheable 동작 =====
        // 첫 번째 호출: 메서드 실행, 캐시 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        int cacheableCount1 = bookService.getFindByIsbnCallCount();

        // 두 번째 호출: 캐시 히트, 메서드 실행 X
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        int cacheableCount2 = bookService.getFindByIsbnCallCount();

        // TODO: @Cacheable은 두 번째 호출에서 메서드를 실행하지 않음
        // 빈칸을 채우세요 (true 또는 false)
        boolean cacheableSkippedExecution = (cacheableCount1 == cacheableCount2);
        assertEquals(_____, cacheableSkippedExecution,
                "@Cacheable은 캐시 히트 시 메서드 실행을 건너뛰나요?");

        // ===== @CachePut 동작 =====
        bookService.resetCallCounts();
        Book book = new Book(ISBN_EFFECTIVE_JAVA, "Test", "Author", 10000);

        // 첫 번째 호출
        bookService.updateBook(book);
        int cachePutCount1 = bookService.getUpdateBookCallCount();

        // 두 번째 호출
        bookService.updateBook(book);
        int cachePutCount2 = bookService.getUpdateBookCallCount();

        // TODO: @CachePut은 매번 메서드를 실행
        // 빈칸을 채우세요 (true 또는 false)
        boolean cachePutAlwaysExecutes = (cachePutCount2 > cachePutCount1);
        assertEquals(_____, cachePutAlwaysExecutes,
                "@CachePut은 항상 메서드를 실행하나요?");
    }

    // =========================================================================
    // 📌 테스트 4: @CachePut으로 새 데이터 캐시에 추가
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("@CachePut: 새로운 데이터를 캐시에 추가")
    void test_CachePut_AddsNewEntry() {
        // Given: 새 ISBN의 책
        String newIsbn = "978-0-00-000000-0";
        Book newBook = new Book(newIsbn, "New Book", "New Author", 30000);

        // When: @CachePut으로 새 데이터 저장
        bookService.updateBook(newBook);

        // Then: @Cacheable로 조회 시 캐시 히트
        bookService.resetCallCounts();
        Book cachedBook = bookService.findByIsbn(newIsbn);

        // TODO: 빈칸을 채우세요
        assertEquals(_____, cachedBook.getTitle(),
                "@CachePut으로 저장한 책 제목");
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "@CachePut이 캐시에 저장했으므로 @Cacheable은 캐시 히트");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: 3
     * 테스트 2: "Effective Java 4th Edition", 55000, 0
     * 테스트 3: true, true
     * 테스트 4: "New Book", 0
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final int _____ = -999;
    private static final String ____S = "FILL_ME";
    private static final boolean ____B = false;  // true 또는 false로 바꾸세요
}
