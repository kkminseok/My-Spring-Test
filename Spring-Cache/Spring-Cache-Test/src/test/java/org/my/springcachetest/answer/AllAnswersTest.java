package org.my.springcachetest.answer;

import org.junit.jupiter.api.*;
import org.my.springcachetest.domain.Book;
import org.my.springcachetest.domain.User;
import org.my.springcachetest.service.BookService;
import org.my.springcachetest.service.SelfInvocationService;
import org.my.springcachetest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * =============================================================================
 * ✅ Spring Cache 전체 정답 테스트
 * =============================================================================
 *
 * 모든 테스트의 정답을 포함한 통합 테스트입니다.
 * 빈칸 테스트를 먼저 풀어보고, 여기서 정답을 확인하세요!
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AllAnswersTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private SelfInvocationService selfInvocationService;

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
        userService.resetCallCounts();
        userService.resetUserStore();
        selfInvocationService.resetCallCounts();
    }

    // =========================================================================
    // @Cacheable 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] @Cacheable 기본 동작")
    void answer_Cacheable_Basic() {
        // 첫 번째 호출: 메서드 실행
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());

        // 두 번째 호출: 캐시 히트 -> 메서드 실행 X
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());  // 여전히 1
    }

    @Test
    @DisplayName("[정답] @Cacheable condition - 조건부 캐싱")
    void answer_Cacheable_Condition() {
        // condition="#isbn.length() > 10"
        // 짧은 ISBN (길이 10): condition=false -> 캐시 사용 X
        bookService.findByIsbnWithCondition("1234567890");
        bookService.findByIsbnWithCondition("1234567890");
        assertEquals(2, bookService.getFindByIsbnCallCount());  // 매번 실행

        bookService.resetCallCounts();

        // 긴 ISBN: condition=true -> 캐시 사용 O
        bookService.findByIsbnWithCondition(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnWithCondition(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());  // 1번만 실행
    }

    @Test
    @DisplayName("[정답] @Cacheable unless - 결과 기반 제외")
    void answer_Cacheable_Unless() {
        // unless="#result == null || #result.hardback"
        // 양장본: unless=true -> 캐시에 저장 X
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        bookService.findByIsbnExcludeHardback(ISBN_SPRING_IN_ACTION);
        assertEquals(2, bookService.getFindByIsbnCallCount());  // 매번 실행

        bookService.resetCallCounts();

        // 일반판: unless=false -> 캐시에 저장 O
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());  // 1번만 실행
    }

    // =========================================================================
    // @CachePut 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] @CachePut - 항상 메서드 실행")
    void answer_CachePut_AlwaysExecutes() {
        Book book = new Book(ISBN_EFFECTIVE_JAVA, "Test", "Author", 10000);

        bookService.updateBook(book);
        bookService.updateBook(book);
        bookService.updateBook(book);

        assertEquals(3, bookService.getUpdateBookCallCount());  // 3번 모두 실행
    }

    @Test
    @DisplayName("[정답] @CachePut - 캐시 갱신")
    void answer_CachePut_UpdatesCache() {
        // 원본 캐시
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        // @CachePut으로 업데이트
        Book updatedBook = new Book(ISBN_EFFECTIVE_JAVA, "Effective Java 4th Edition", "Joshua Bloch", 55000);
        bookService.updateBook(updatedBook);

        // 캐시에서 조회
        bookService.resetCallCounts();
        Book cachedBook = bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);

        assertEquals("Effective Java 4th Edition", cachedBook.getTitle());
        assertEquals(55000, cachedBook.getPrice());
        assertEquals(0, bookService.getFindByIsbnCallCount());  // 캐시 히트
    }

    // =========================================================================
    // @CacheEvict 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] @CacheEvict - 특정 키 삭제")
    void answer_CacheEvict_SingleKey() {
        // 캐시에 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());

        // 캐시 히트 확인
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(1, bookService.getFindByIsbnCallCount());

        // 캐시 삭제
        bookService.deleteBook(ISBN_EFFECTIVE_JAVA);

        // 다시 조회 - 캐시 미스
        bookService.addBook(new Book(ISBN_EFFECTIVE_JAVA, "Effective Java", "Joshua Bloch", 45000));
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        assertEquals(2, bookService.getFindByIsbnCallCount());  // 메서드 재실행
    }

    @Test
    @DisplayName("[정답] @CacheEvict allEntries - 전체 삭제")
    void answer_CacheEvict_AllEntries() {
        // 여러 항목 캐시
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        bookService.findByIsbn(ISBN_SPRING_IN_ACTION);
        assertEquals(3, bookService.getFindByIsbnCallCount());

        // 전체 삭제
        bookService.clearAllBooks();

        // 다시 조회 - 모두 캐시 미스
        bookService.resetBookStore();
        bookService.resetCallCounts();
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbn(ISBN_JAVASCRIPT);
        bookService.findByIsbn(ISBN_SPRING_IN_ACTION);
        assertEquals(3, bookService.getFindByIsbnCallCount());  // 3번 모두 실행
    }

    // =========================================================================
    // @Caching 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] @Caching - 다중 캐시 삭제")
    void answer_Caching_MultipleEvict() {
        // 두 캐시에 저장
        bookService.findByIsbn(ISBN_EFFECTIVE_JAVA);
        bookService.findByIsbnExcludeHardback(ISBN_EFFECTIVE_JAVA);

        // 두 캐시 모두에서 삭제
        bookService.deleteBookFromAllCaches(ISBN_EFFECTIVE_JAVA);

        // 확인
        Cache booksCache = cacheManager.getCache("books");
        Cache booksNoHardbackCache = cacheManager.getCache("booksNoHardback");

        assertNull(booksCache.get(ISBN_EFFECTIVE_JAVA));
        assertNull(booksNoHardbackCache.get(ISBN_EFFECTIVE_JAVA));
    }

    // =========================================================================
    // @CacheConfig 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] @CacheConfig - 기본 캐시 이름 사용")
    void answer_CacheConfig_DefaultCacheName() {
        userService.findById(1L);

        Cache usersCache = cacheManager.getCache("users");
        assertNotNull(usersCache);
        assertNotNull(usersCache.get(1L));  // "users" 캐시에 저장됨
    }

    @Test
    @DisplayName("[정답] @CacheConfig - 오버라이드")
    void answer_CacheConfig_Override() {
        userService.findPremiumUser(1L);

        Cache premiumUsersCache = cacheManager.getCache("premiumUsers");
        Cache usersCache = cacheManager.getCache("users");

        assertNotNull(premiumUsersCache.get(1L));  // premiumUsers에 저장
        assertNull(usersCache.get(1L));            // users에는 없음
    }

    // =========================================================================
    // SpEL 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] SpEL - #p0 파라미터 인덱스")
    void answer_SpEL_ParameterIndex() {
        userService.findByIdUsingIndex(1L);
        userService.findByIdUsingIndex(1L);
        assertEquals(1, userService.getFindByIdCallCount());  // 캐시 동작
    }

    @Test
    @DisplayName("[정답] SpEL - #user.id 객체 속성")
    void answer_SpEL_ObjectProperty() {
        User user = new User(1L, "alice", "alice@example.com", 25);
        userService.updateUser(user);

        Cache usersCache = cacheManager.getCache("users");
        assertNotNull(usersCache.get(1L));  // key=#user.id 로 저장됨
    }

    @Test
    @DisplayName("[정답] SpEL - #root.methodName")
    void answer_SpEL_RootMethodName() {
        userService.findByIdWithMethodName(1L);

        Cache usersCache = cacheManager.getCache("users");
        assertNotNull(usersCache.get("findByIdWithMethodName_1"));  // 메서드명_id 형식
    }

    @Test
    @DisplayName("[정답] SpEL - 복합 condition과 unless")
    void answer_SpEL_ComplexCondition() {
        // condition="#id != null && #id > 0", unless="#result == null || !#result.active"

        // 활성 사용자: 캐시됨
        userService.findActiveUser(1L);
        userService.findActiveUser(1L);
        assertEquals(1, userService.getFindByIdCallCount());

        userService.resetCallCounts();

        // 비활성 사용자: unless 조건으로 캐시 안됨
        userService.findActiveUser(3L);
        userService.findActiveUser(3L);
        assertEquals(2, userService.getFindByIdCallCount());
    }

    // =========================================================================
    // Self-Invocation 정답
    // =========================================================================
    @Test
    @DisplayName("[정답] Self-Invocation - 외부 호출 vs 내부 호출")
    void answer_SelfInvocation() {
        // 외부 호출: 캐시 동작
        selfInvocationService.getCachedData("key1");
        selfInvocationService.getCachedData("key1");
        assertEquals(1, selfInvocationService.getCachedMethodCallCount());  // 1번만 실행

        selfInvocationService.resetCallCounts();
        cacheManager.getCache("selfInvocationTest").clear();

        // Self-Invocation: 캐시 미동작
        selfInvocationService.wrapperMethod("key1");
        selfInvocationService.wrapperMethod("key1");
        assertEquals(2, selfInvocationService.getCachedMethodCallCount());  // 2번 모두 실행
    }
}
