package org.my.springcachetest.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * ⚠️ Spring Cache 프록시 제한(Self-Invocation) 학습용 서비스
 * =============================================================================
 *
 * Spring AOP 프록시 모드에서의 중요한 제한사항을 보여줍니다.
 * 같은 클래스 내에서 캐시 메서드를 호출하면 캐시가 동작하지 않습니다!
 *
 * 💡 왜 이런 현상이 발생하나요?
 * - Spring은 프록시 패턴을 사용해 캐시를 구현
 * - 외부에서 호출할 때: 클라이언트 -> 프록시 -> 실제 객체 (캐시 동작 O)
 * - 내부에서 호출할 때: 실제 객체 -> 실제 객체 (프록시를 거치지 않음, 캐시 동작 X)
 */
@Service
public class SelfInvocationService {

    private final AtomicInteger cachedMethodCallCount = new AtomicInteger(0);
    private final AtomicInteger wrapperMethodCallCount = new AtomicInteger(0);

    // =========================================================================
    // 📌 캐시가 적용된 메서드
    // =========================================================================
    @Cacheable("selfInvocationTest")
    public String getCachedData(String key) {
        cachedMethodCallCount.incrementAndGet();
        simulateSlowService();
        return "Data for: " + key;
    }

    // =========================================================================
    // 📌 ❌ Self-Invocation 문제 발생!
    // =========================================================================
    /**
     * 같은 클래스 내에서 getCachedData()를 호출
     * 이 경우 캐시가 동작하지 않습니다!
     *
     * 💡 학습 포인트:
     * - wrapperMethod() -> getCachedData() 호출 시 프록시를 거치지 않음
     * - 따라서 getCachedData()가 매번 실행됨
     */
    public String wrapperMethod(String key) {
        wrapperMethodCallCount.incrementAndGet();
        // ⚠️ 이 호출은 캐시를 사용하지 않습니다!
        return "Wrapper: " + getCachedData(key);
    }

    // =========================================================================
    // 📌 참고: 해결 방법들
    // =========================================================================
    /*
     * Self-Invocation 문제 해결 방법:
     *
     * 1. 별도의 Bean으로 분리
     *    - 캐시 메서드를 다른 서비스로 이동
     *
     * 2. AspectJ 모드 사용
     *    - @EnableCaching(mode = AdviceMode.ASPECTJ)
     *    - 컴파일 타임 위빙 또는 로드 타임 위빙 필요
     *
     * 3. Self-injection (권장하지 않음)
     *    - @Lazy를 사용해 자기 자신을 주입
     *    - 순환 의존성 문제 발생 가능
     */

    private void simulateSlowService() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getCachedMethodCallCount() {
        return cachedMethodCallCount.get();
    }

    public int getWrapperMethodCallCount() {
        return wrapperMethodCallCount.get();
    }

    public void resetCallCounts() {
        cachedMethodCallCount.set(0);
        wrapperMethodCallCount.set(0);
    }
}
