package org.my.springcachetest;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Book;
import org.my.springcachetest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * 🔄 @Caching 학습 테스트 - 다중 캐시 작업
 * =============================================================================
 *
 * @Caching 어노테이션을 사용하면 여러 캐시 작업을 한 메서드에서 수행할 수 있습니다.
 *
 * 사용 가능한 속성:
 * - cacheable: @Cacheable 배열
 * - put: @CachePut 배열
 * - evict: @CacheEvict 배열
 *
 * 💡 사용 케이스:
 * - 하나의 메서드가 여러 캐시에 영향을 줄 때
 * - 데이터 삭제 시 관련된 모든 캐시를 한번에 정리할 때
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachingTest {

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
    // 📌 테스트 1: @Caching으로 여러 캐시 동시 삭제
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("@Caching: 여러 캐시를 한번에 삭제")
    void test_Caching_MultipleEvict() {
        // Given: "books" 캐시와 "booksNoHardback" 캐시에 각각 데이터 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);          // books 캐시
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);  // booksNoHardback 캐시

        // 캐시 저장 확인
        Cache booksCache = cacheManager.getCache("books");
        Cache booksNoHardbackCache = cacheManager.getCache("booksNoHardback");

        assertNotNull(booksCache.get(ISBN_EFFECTIVE_JAVA), "books 캐시에 저장됨");
        assertNotNull(booksNoHardbackCache.get(ISBN_EFFECTIVE_JAVA), "booksNoHardback 캐시에 저장됨");

        // When: @Caching으로 두 캐시 모두에서 삭제
        bookService.deleteBookFromAllCaches(ISBN_EFFECTIVE_JAVA);

        // Then: 두 캐시 모두에서 삭제됨
        // TODO: 빈칸을 채우세요 (null 또는 not null)
        var booksResult = booksCache.get(ISBN_EFFECTIVE_JAVA);
        var noHardbackResult = booksNoHardbackCache.get(ISBN_EFFECTIVE_JAVA);

        // 삭제 후 캐시에서 조회하면 null
        assertNull(booksResult, "books 캐시에서 삭제됨");
        assertNull(noHardbackResult, "booksNoHardback 캐시에서 삭제됨");
    }

    // =========================================================================
    // 📌 테스트 2: @Caching - 선택적 캐시만 영향받음
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("@Caching: 지정된 캐시만 영향받고 다른 캐시는 유지")
    void test_Caching_OnlySpecifiedCaches() {
        // Given: 다양한 캐시에 데이터 저장
        String isbn1 = ISBN_EFFECTIVE_JAVA;
        String isbn2 = "978-0-596-51774-8";

        bookService.findByIsbn(isbn1);           // books 캐시
        bookService.findByIsbn(isbn2);           // books 캐시
        bookService.findByIsbnExcludeHardback(isbn1);  // booksNoHardback 캐시

        // When: isbn1에 대해 @Caching으로 삭제
        bookService.deleteBookFromAllCaches(isbn1);

        // Then:
        // - isbn1: books, booksNoHardback 모두에서 삭제됨
        // - isbn2: books 캐시에 여전히 존재

        Cache booksCache = cacheManager.getCache("books");

        // TODO: 빈칸을 채우세요
        // isbn1은 삭제되어 null, isbn2는 유지되어 not null
        boolean isbn1Deleted = (booksCache.get(isbn1) == null);
        boolean isbn2Exists = (booksCache.get(isbn2) != null);

        assertEquals(_____, isbn1Deleted, "isbn1은 삭제되었나요?");
        assertEquals(_____, isbn2Exists, "isbn2는 유지되었나요?");
    }

    // =========================================================================
    // 📌 테스트 3: @Caching 후 메서드 재호출
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("@Caching: 삭제 후 재조회 시 캐시 미스")
    void test_Caching_AfterEvictCacheMiss() {
        // Given: 캐시에 데이터 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);

        // 캐시 히트 확인
        bookService.resetCallCounts();
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(0, bookService.getFindByIsbnCallCount(), "캐시 히트");

        // When: @Caching으로 삭제
        bookService.deleteBookFromAllCaches(ISBN_EFFECTIVE_JAVA);

        // 데이터 복원 (삭제된 책 다시 추가)
        bookService.addBook(new Book(ISBN_EFFECTIVE_JAVA, "Effective Java", "Joshua Bloch", 45000));
        bookService.resetCallCounts();

        // Then: 캐시 미스, 메서드 재실행
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);

        // TODO: 빈칸을 채우세요
        // 두 메서드 모두 캐시 미스이므로 각각 1번씩 실행
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "두 종류의 캐시에서 모두 삭제되어 메서드가 몇 번 실행되나요?");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: 둘 다 null (assertNull 사용)
     * 테스트 2: true, true
     * 테스트 3: 2
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final int _____ = -999;
    private static final boolean ____B = false;
}
