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
 * 📚 @Cacheable 학습 테스트
 * =============================================================================
 *
 * 이 테스트를 통해 @Cacheable의 동작 방식을 학습합니다.
 * 빈칸(_____)을 채워서 테스트를 통과시켜 보세요!
 *
 * 💡 힌트:
 * - 캐시 히트: 메서드가 실행되지 않고 캐시된 값 반환
 * - 캐시 미스: 메서드가 실행되고 결과가 캐시에 저장됨
 * - callCount를 통해 실제 메서드 호출 횟수 확인 가능
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheableTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    private static final String ISBN_EFFECTIVE_JAVA = "978-0-13-468599-1";
    private static final String ISBN_JAVASCRIPT = "978-0-596-51774-8";
    private static final String ISBN_SPRING_IN_ACTION = "978-1-61729-800-0";  // 양장본

    @BeforeEach
    void setUp() {
        // 각 테스트 전 캐시와 카운터 초기화
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear()
        );
        bookService.resetCallCounts();
        bookService.resetBookStore();
    }

    // =========================================================================
    // 📌 테스트 1: @Cacheable 기본 동작
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("@Cacheable 기본: 첫 번째 호출은 메서드 실행, 두 번째는 캐시 히트")
    void test_Cacheable_Basic() {
        // Given: 카운터가 0인 상태
        assertEquals(0, bookService.getFindByIsbnCallCount());

        // When: 첫 번째 호출
        Book firstCall = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // Then: 메서드가 실행되었으므로 카운터는 1
        // TODO: 빈칸을 채우세요
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "첫 번째 호출 후 카운터 값은?");

        // When: 같은 ISBN으로 두 번째 호출
        Book secondCall = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // Then: 캐시 히트! 메서드가 실행되지 않으므로 카운터는 여전히 1
        // TODO: 빈칸을 채우세요
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "캐시 히트 후 카운터 값은?");

        // 반환된 객체는 동일해야 함
        assertEquals(firstCall.getTitle(), secondCall.getTitle());
    }

    // =========================================================================
    // 📌 테스트 2: 다른 키는 캐시 미스
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("@Cacheable: 다른 키로 호출하면 캐시 미스")
    void test_Cacheable_DifferentKey() {
        // When: 첫 번째 ISBN으로 호출
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        int countAfterFirst = bookService.getFindByIsbnCallCount();

        // When: 다른 ISBN으로 호출
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        int countAfterSecond = bookService.getFindByIsbnCallCount();

        // Then: 다른 키이므로 캐시 미스, 메서드가 다시 실행됨
        // TODO: 빈칸을 채우세요
        assertEquals(_____, countAfterSecond - countAfterFirst,
                "다른 키로 호출하면 메서드가 몇 번 더 실행되나요?");
    }

    // =========================================================================
    // 📌 테스트 3: 커스텀 키 - 특정 파라미터만 키로 사용
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("@Cacheable key 속성: 특정 파라미터를 키로 지정")
    void test_Cacheable_CustomKey() {
        // Given: key="#title"로 설정된 findByTitle 메서드
        String title = "Effective Java";

        // When: 같은 title, 다른 includeOutOfPrint 값으로 호출
        bookService.findByTitle(title, true);
        int countAfterFirst = bookService.getFindByTitleCallCount();

        bookService.findByTitle(title, false);  // includeOutOfPrint가 다름
        int countAfterSecond = bookService.getFindByTitleCallCount();

        // Then: key="#title"이므로 includeOutOfPrint 값이 달라도 캐시 히트
        // TODO: 빈칸을 채우세요 (countAfterFirst와 countAfterSecond가 같은가요?)
        assertEquals(_____, countAfterSecond,
                "key=#title이므로 두 번째 파라미터가 달라도 캐시 히트해야 합니다");
    }

    // =========================================================================
    // 📌 테스트 4: condition - 조건부 캐싱
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("@Cacheable condition: 조건이 false면 캐시 사용 안함")
    void test_Cacheable_Condition() {
        // Given: condition="#isbn.length() > 10" 으로 설정됨
        String shortIsbn = "1234567890";      // 길이 10 - condition false
        String longIsbn = ISBN_EFFECTIVE_JAVA; // 길이 > 10 - condition true

        // When: 짧은 ISBN으로 두 번 호출
        bookService.findByIsbnWithCondition(shortIsbn);
        bookService.findByIsbnWithCondition(shortIsbn);
        int countForShortIsbn = bookService.getFindByIsbnCallCount();

        bookService.resetCallCounts();

        // When: 긴 ISBN으로 두 번 호출
        bookService.findByIsbnWithCondition(longIsbn);
        bookService.findByIsbnWithCondition(longIsbn);
        int countForLongIsbn = bookService.getFindByIsbnCallCount();

        // Then:
        // - 짧은 ISBN: condition=false이므로 매번 메서드 실행 (캐시 사용 X)
        // - 긴 ISBN: condition=true이므로 첫 번째만 실행, 두 번째는 캐시 히트
        // TODO: 빈칸을 채우세요
        assertEquals(_____, countForShortIsbn, "condition=false면 캐시를 사용하지 않음");
        assertEquals(_____, countForLongIsbn, "condition=true면 캐시를 사용함");
    }

    // =========================================================================
    // 📌 테스트 5: unless - 결과 기반 캐싱 제외
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("@Cacheable unless: 결과가 조건에 맞으면 캐시에 저장 안함")
    void test_Cacheable_Unless() {
        // Given: unless="#result == null || #result.hardback" 으로 설정됨
        // ISBN_SPRING_IN_ACTION은 양장본(hardback=true)

        // When: 양장본 조회 두 번
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        int countForHardback = bookService.getFindByIsbnCallCount();

        bookService.resetCallCounts();

        // When: 일반판 조회 두 번
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        int countForNormal = bookService.getFindByIsbnCallCount();

        // Then:
        // - 양장본: unless 조건에 해당하므로 캐시에 저장 안됨 -> 매번 실행
        // - 일반판: 캐시에 저장됨 -> 두 번째는 캐시 히트
        // TODO: 빈칸을 채우세요
        assertEquals(_____, countForHardback, "양장본은 캐시에 저장되지 않음");
        assertEquals(_____, countForNormal, "일반판은 캐시에 저장됨");
    }

    // =========================================================================
    // 📌 테스트 6: null 결과 처리
    // =========================================================================
    @Test
    @Order(6)
    @DisplayName("@Cacheable: 존재하지 않는 키로 조회 시 null도 캐시될 수 있음")
    void test_Cacheable_NullResult() {
        // Given: 존재하지 않는 ISBN
        String nonExistentIsbn = "000-0-00-000000-0";

        // When: 존재하지 않는 ISBN으로 두 번 조회 (기본 findByIsbn - unless 없음)
        Book firstCall = bookService.findByIsbn(nonExistentIsbn);
        Book secondCall = bookService.findByIsbn(nonExistentIsbn);

        // Then: null도 캐시되므로 두 번째는 캐시 히트
        assertNull(firstCall);
        assertNull(secondCall);

        // TODO: 빈칸을 채우세요
        // 기본 @Cacheable은 null도 캐시하므로 메서드는 1번만 실행됨
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "기본 @Cacheable은 null도 캐시합니다");
    }

    // =========================================================================
    // 📌 테스트 7: unless로 null 캐싱 방지
    // =========================================================================
    @Test
    @Order(7)
    @DisplayName("@Cacheable unless: null 결과 캐싱 방지")
    void test_Cacheable_UnlessNull() {
        // Given: unless="#result == null || #result.hardback" 으로 설정됨
        String nonExistentIsbn = "000-0-00-000000-0";

        // When: 존재하지 않는 ISBN으로 두 번 조회
        bookService.findByIsbnExcludeHardback(nonExistentIsbn);
        bookService.findByIsbnExcludeHardback(nonExistentIsbn);

        // Then: unless 조건에 의해 null은 캐시되지 않으므로 매번 실행
        // TODO: 빈칸을 채우세요
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "unless로 null을 제외하면 매번 메서드가 실행됩니다");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: 1, 1
     * 테스트 2: 1
     * 테스트 3: countAfterFirst (= 1)
     * 테스트 4: 2, 1
     * 테스트 5: 2, 1
     * 테스트 6: 1
     * 테스트 7: 2
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더 (이 변수를 실제 값으로 바꾸세요)
    // =========================================================================
    private static final int _____ = -999;  // TODO: 실제 값으로 교체하세요
}
