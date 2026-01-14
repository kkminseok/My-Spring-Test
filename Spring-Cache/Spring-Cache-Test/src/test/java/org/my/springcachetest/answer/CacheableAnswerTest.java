package org.my.springcachetest.answer;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Book;
import org.my.springcachetest.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * ✅ @Cacheable 정답 테스트
 * =============================================================================
 *
 * 이 파일은 CacheableTest.java의 정답 버전입니다.
 * 먼저 빈칸을 채워보고, 맞는지 확인해보세요!
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheableAnswerTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    private static final String ISBN_EFFECTIVE_JAVA = "978-0-13-468599-1";
    private static final String ISBN_JAVASCRIPT = "978-0-596-51774-8";
    private static final String ISBN_SPRING_IN_ACTION = "978-1-61729-800-0";

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        bookService.resetCallCounts();
        bookService.resetBookStore();
    }

    @Test
    @Order(1)
    @DisplayName("@Cacheable 기본: 첫 번째 호출은 메서드 실행, 두 번째는 캐시 히트")
    void test_Cacheable_Basic() {
        assertEquals(0, bookService.getFindByIsbnCallCount());

        Book firstCall = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount(), "첫 번째 호출 후 카운터 값은 1");

        Book secondCall = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount(), "캐시 히트 후에도 카운터는 여전히 1");

        assertEquals(firstCall.getTitle(), secondCall.getTitle());
    }

    @Test
    @Order(2)
    @DisplayName("@Cacheable: 다른 키로 호출하면 캐시 미스")
    void test_Cacheable_DifferentKey() {
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        int countAfterFirst = bookService.getFindByIsbnCallCount();

        bookService.findByIsbn(ISBN_JAVASCRIPT);
        int countAfterSecond = bookService.getFindByIsbnCallCount();

        assertEquals(1, countAfterSecond - countAfterFirst, "다른 키로 호출하면 메서드가 1번 더 실행됨");
    }

    @Test
    @Order(3)
    @DisplayName("@Cacheable key 속성: 특정 파라미터를 키로 지정")
    void test_Cacheable_CustomKey() {
        String title = "Effective Java";

        bookService.findByTitle(title, true);
        int countAfterFirst = bookService.getFindByTitleCallCount();

        bookService.findByTitle(title, false);
        int countAfterSecond = bookService.getFindByTitleCallCount();

        assertEquals(countAfterFirst, countAfterSecond,
                "key=#title이므로 두 번째 파라미터가 달라도 캐시 히트");
    }

    @Test
    @Order(4)
    @DisplayName("@Cacheable condition: 조건이 false면 캐시 사용 안함")
    void test_Cacheable_Condition() {
        String shortIsbn = "1234567890";      // 길이 10 - condition false
        String longIsbn = ISBN_EFFECTIVE_JAVA; // 길이 > 10 - condition true

        bookService.findByIsbnWithCondition(shortIsbn);
        bookService.findByIsbnWithCondition(shortIsbn);
        int countForShortIsbn = bookService.getFindByIsbnCallCount();

        bookService.resetCallCounts();

        bookService.findByIsbnWithCondition(longIsbn);
        bookService.findByIsbnWithCondition(longIsbn);
        int countForLongIsbn = bookService.getFindByIsbnCallCount();

        assertEquals(2, countForShortIsbn, "condition=false면 캐시를 사용하지 않음 (매번 실행)");
        assertEquals(1, countForLongIsbn, "condition=true면 캐시를 사용함 (1번만 실행)");
    }

    @Test
    @Order(5)
    @DisplayName("@Cacheable unless: 결과가 조건에 맞으면 캐시에 저장 안함")
    void test_Cacheable_Unless() {
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        int countForHardback = bookService.getFindByIsbnCallCount();

        bookService.resetCallCounts();

        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        int countForNormal = bookService.getFindByIsbnCallCount();

        assertEquals(2, countForHardback, "양장본은 캐시에 저장되지 않음 (매번 실행)");
        assertEquals(1, countForNormal, "일반판은 캐시에 저장됨 (1번만 실행)");
    }

    @Test
    @Order(6)
    @DisplayName("@Cacheable: 존재하지 않는 키로 조회 시 null도 캐시될 수 있음")
    void test_Cacheable_NullResult() {
        String nonExistentIsbn = "000-0-00-000000-0";

        Book firstCall = bookService.findByIsbn(nonExistentIsbn);
        Book secondCall = bookService.findByIsbn(nonExistentIsbn);

        assertNull(firstCall);
        assertNull(secondCall);

        assertEquals(1, bookService.getFindByIsbnCallCount(),
                "기본 @Cacheable은 null도 캐시합니다 (1번만 실행)");
    }

    @Test
    @Order(7)
    @DisplayName("@Cacheable unless: null 결과 캐싱 방지")
    void test_Cacheable_UnlessNull() {
        String nonExistentIsbn = "000-0-00-000000-0";

        bookService.findByIsbnExcludeHardback(nonExistentIsbn);
        bookService.findByIsbnExcludeHardback(nonExistentIsbn);

        assertEquals(2, bookService.getFindByIsbnCallCount(),
                "unless로 null을 제외하면 매번 메서드가 실행됩니다");
    }
}
