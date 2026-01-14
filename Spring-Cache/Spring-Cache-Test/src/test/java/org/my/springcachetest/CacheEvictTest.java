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
 * 🗑️ @CacheEvict 학습 테스트
 * =============================================================================
 *
 * @CacheEvict는 캐시에서 데이터를 삭제합니다.
 *
 * 주요 속성:
 * - key: 삭제할 캐시 키 (SpEL)
 * - allEntries: true면 해당 캐시의 모든 항목 삭제
 * - beforeInvocation: true면 메서드 실행 전 삭제, false(기본)면 실행 후 삭제
 *
 * 💡 사용 케이스:
 * - 데이터 삭제 시 캐시도 함께 삭제
 * - 대량 데이터 변경 후 캐시 전체 무효화
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheEvictTest {

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

    // =========================================================================
    // 📌 테스트 1: @CacheEvict 기본 동작 - 특정 키 삭제
    // =========================================================================
    @Test
    @Order(1)
    @DisplayName("@CacheEvict: 특정 키의 캐시 항목 삭제")
    void test_CacheEvict_SingleKey() {
        // Given: 캐시에 데이터 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());

        // 캐시 히트 확인
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount(), "캐시 히트");

        // When: @CacheEvict로 해당 키 삭제
        bookService.deleteBook(ISBN_EFFECTIVE_JAVA);

        // Then: 캐시가 삭제되었으므로 다시 메서드 실행
        bookService.addBook(new Book(ISBN_EFFECTIVE_JAVA, "Effective Java", "Joshua Bloch", 45000));
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // TODO: 빈칸을 채우세요
        // 캐시가 삭제되었으므로 메서드가 다시 실행됨
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "@CacheEvict 후 메서드 호출 횟수는?");
    }

    // =========================================================================
    // 📌 테스트 2: @CacheEvict는 다른 키에 영향 없음
    // =========================================================================
    @Test
    @Order(2)
    @DisplayName("@CacheEvict: 다른 키의 캐시는 유지됨")
    void test_CacheEvict_OnlySpecificKey() {
        // Given: 여러 책을 캐시에 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        assertEquals(2, bookService.getFindByIsbnCallCount());

        // When: EFFECTIVE_JAVA만 삭제
        bookService.deleteBook(ISBN_EFFECTIVE_JAVA);

        bookService.resetCallCounts();

        // Then: JAVASCRIPT는 여전히 캐시 히트
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        int countForJavascript = bookService.getFindByIsbnCallCount();

        // EFFECTIVE_JAVA는 캐시 미스 (삭제됨)
        bookService.addBook(new Book(ISBN_EFFECTIVE_JAVA, "Effective Java", "Joshua Bloch", 45000));
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        int countAfterEffectiveJava = bookService.getFindByIsbnCallCount();

        // TODO: 빈칸을 채우세요
        assertEquals(_____, countForJavascript, "JAVASCRIPT는 캐시 히트 (삭제 안됨)");
        assertEquals(_____, countAfterEffectiveJava, "EFFECTIVE_JAVA는 캐시 미스 (삭제됨)");
    }

    // =========================================================================
    // 📌 테스트 3: allEntries=true - 캐시 전체 삭제
    // =========================================================================
    @Test
    @Order(3)
    @DisplayName("@CacheEvict allEntries: 캐시의 모든 항목 삭제")
    void test_CacheEvict_AllEntries() {
        // Given: 여러 책을 캐시에 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        bookService.findByIsbn(ISBN_SPRING_IN_ACTION);
        assertEquals(3, bookService.getFindByIsbnCallCount());

        // 모두 캐시 히트 확인
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        bookService.findByIsbn(ISBN_SPRING_IN_ACTION);
        assertEquals(3, bookService.getFindByIsbnCallCount(), "모두 캐시 히트");

        // When: allEntries=true로 전체 삭제
        bookService.clearAllBooks();

        // Then: 모든 캐시가 삭제됨
        bookService.resetBookStore();  // 데이터 복원
        bookService.resetCallCounts();

        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        bookService.findByIsbn(ISBN_SPRING_IN_ACTION);

        // TODO: 빈칸을 채우세요
        // 모든 캐시가 삭제되었으므로 전부 메서드 실행
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "allEntries=true로 삭제 후 메서드 호출 횟수는?");
    }

    // =========================================================================
    // 📌 테스트 4: beforeInvocation=true vs false
    // =========================================================================
    @Test
    @Order(4)
    @DisplayName("@CacheEvict beforeInvocation: 메서드 실행 전/후 삭제 시점")
    void test_CacheEvict_BeforeInvocation() {
        // Given: 캐시에 데이터 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // ===== beforeInvocation=true 테스트 =====
        // 예외가 발생해도 캐시는 이미 삭제된 상태
        try {
            // INVALID로 시작하는 ISBN은 예외 발생
            bookService.deleteBookBeforeInvocation("INVALID-ISBN");
        } catch (IllegalArgumentException e) {
            // 예외 발생 예상
        }

        // 원래 ISBN으로 조회 - beforeInvocation이므로 전혀 다른 키가 사용됨
        // 이 테스트에서는 deleteBookBeforeInvocation이 isbn을 키로 사용
        // 따라서 "INVALID-ISBN" 캐시만 삭제 시도됨 (존재하지 않으므로 무시)

        // 새로운 시나리오: 실제 ISBN으로 테스트
        bookService.resetCallCounts();

        // 캐시에 있는 ISBN 조회
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // TODO: 빈칸을 채우세요
        // 아까 저장한 캐시가 그대로이므로 캐시 히트
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "다른 키의 캐시는 영향 없음");

        // ===== beforeInvocation=true의 특성 테스트 =====
        // beforeInvocation=true: 예외가 발생해도 캐시 삭제됨
        bookService.resetCallCounts();
        bookService.findByIsbn(ISBN_JAVASCRIPT);  // 캐시에 저장

        try {
            // 먼저 해당 키의 캐시를 삭제한 후 예외 발생
            bookService.deleteBookBeforeInvocation(ISBN_JAVASCRIPT);
        } catch (Exception e) {
            // deleteBookBeforeInvocation은 INVALID로 시작하는 경우만 예외
        }

        // ISBN_JAVASCRIPT 캐시가 삭제되었는지 확인
        bookService.findByIsbn(ISBN_JAVASCRIPT);

        // beforeInvocation=true이므로 메서드 실행 전에 캐시 삭제됨
        // (예외 발생 여부와 관계없이)
        assertEquals(2, bookService.getFindByIsbnCallCount(),
                "beforeInvocation=true면 메서드 실행 전 캐시 삭제");
    }

    // =========================================================================
    // 📌 테스트 5: @CacheEvict와 void 메서드
    // =========================================================================
    @Test
    @Order(5)
    @DisplayName("@CacheEvict: void 메서드에서도 동작")
    void test_CacheEvict_VoidMethod() {
        // Given: 캐시에 데이터 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // When: void 메서드로 캐시 삭제
        bookService.deleteBook(ISBN_EFFECTIVE_JAVA);

        // Then: void 메서드도 정상적으로 캐시 삭제
        bookService.addBook(new Book(ISBN_EFFECTIVE_JAVA, "Effective Java", "Joshua Bloch", 45000));
        bookService.resetCallCounts();
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // TODO: 빈칸을 채우세요
        assertEquals(_____, bookService.getFindByIsbnCallCount(),
                "void 메서드도 캐시를 삭제할 수 있습니다");
    }

    // =========================================================================
    // 정답 확인용 (테스트 실행 후 주석 해제)
    // =========================================================================
    /*
     * 정답:
     * 테스트 1: 2
     * 테스트 2: 0, 1
     * 테스트 3: 3
     * 테스트 4: 0
     * 테스트 5: 1
     */

    // =========================================================================
    // 💡 빈칸 플레이스홀더
    // =========================================================================
    private static final int _____ = -999;
}
