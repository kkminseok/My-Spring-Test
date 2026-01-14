package org.my.springcachetest.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

/**
 * =============================================================================
 * 🔧 Spring Cache 심화 설정
 * =============================================================================
 *
 * 이 설정 클래스에서 다루는 심화 기능들:
 *
 * 1. KeyGenerator - 캐시 키 생성 전략 커스터마이징
 * 2. CacheResolver - 런타임에 사용할 캐시 결정
 * 3. 다중 CacheManager - 서로 다른 캐시 전략 적용
 * 4. CachingConfigurer - 전역 캐시 설정
 *
 * 💡 학습 포인트:
 * - 기본 SimpleKeyGenerator vs 커스텀 KeyGenerator
 * - CacheResolver로 동적 캐시 선택
 * - Primary CacheManager와 Named CacheManager의 차이
 */
@Configuration
public class AdvancedCacheConfig implements CachingConfigurer {

    // =========================================================================
    // 📌 1. 다중 CacheManager 설정
    // =========================================================================

    /**
     * 기본(Primary) 캐시 매니저
     * - @Primary 어노테이션으로 기본 캐시 매니저로 지정
     * - cacheManager를 명시하지 않으면 이 매니저가 사용됨
     *
     * 💡 학습 포인트:
     * - ConcurrentMapCacheManager는 메모리 내 캐시 (개발/테스트용)
     * - 실제 운영에서는 Redis, Caffeine 등 사용
     */
    @Bean
    @Primary
    public CacheManager primaryCacheManager() {
        // 기존 서비스들 + 심화 기능용 캐시 이름 모두 포함
        return new ConcurrentMapCacheManager(
                "books", "booksNoHardback", "advancedBooks", "syncBooks",  // 심화 테스트용
                "users", "activeUsers", "premiumUsers",  // UserService용
                "selfInvocationTest",  // SelfInvocationService용
                // 시나리오 테스트용
                "products", "expensiveProducts", "availableProducts",
                "customerProducts", "popularProducts", "filteredProducts"
        );
    }

    /**
     * 보조 캐시 매니저 - VIP 전용 캐시
     * - 특정 메서드에서 cacheManager = "vipCacheManager"로 지정하여 사용
     *
     * 💡 학습 포인트:
     * - 여러 캐시 매니저를 두어 다른 캐시 전략 적용 가능
     * - 예: VIP 고객은 별도 캐시, 일반 고객은 기본 캐시
     */
    @Bean
    public CacheManager vipCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("vipBooks")));
        return cacheManager;
    }

    /**
     * SimpleCacheManager를 사용한 커스텀 캐시 매니저
     * - 캐시를 직접 생성하고 관리
     *
     * 💡 학습 포인트:
     * - SimpleCacheManager는 미리 정의된 캐시만 사용
     * - 동적 캐시 생성이 필요 없을 때 유용
     */
    @Bean
    public CacheManager customCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("customCache1"),
                new ConcurrentMapCache("customCache2")
        ));
        return cacheManager;
    }

    // =========================================================================
    // 📌 2. KeyGenerator 설정
    // =========================================================================

    /**
     * 커스텀 KeyGenerator - 메서드명 포함
     *
     * 💡 학습 포인트:
     * - 기본 SimpleKeyGenerator: 파라미터만으로 키 생성
     * - 커스텀 KeyGenerator: 클래스명, 메서드명 등 추가 정보 포함 가능
     *
     * 예시:
     * - SimpleKey: [param1, param2]
     * - 커스텀: "ClassName.methodName:[param1, param2]"
     *
     * ⚠️ 사용 시나리오:
     * - 서로 다른 메서드가 같은 파라미터로 호출될 때 충돌 방지
     * - 디버깅 시 키 추적이 용이
     */
    @Bean("customKeyGenerator")
    public KeyGenerator customKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getSimpleName());
                sb.append(".");
                sb.append(method.getName());
                sb.append(":");
                sb.append(Arrays.toString(params));
                return sb.toString();
            }
        };
    }

    /**
     * ISBN 전용 KeyGenerator
     * - ISBN의 마지막 숫자(체크섬)를 제외하고 키 생성
     *
     * 💡 학습 포인트:
     * - 비즈니스 로직에 맞는 키 생성 가능
     * - 파라미터 가공 후 키로 사용
     */
    @Bean("isbnKeyGenerator")
    public KeyGenerator isbnKeyGenerator() {
        return (target, method, params) -> {
            if (params.length > 0 && params[0] instanceof String isbn) {
                // ISBN 체크섬(마지막 문자) 제외하고 키 생성
                return isbn.length() > 1 ? isbn.substring(0, isbn.length() - 1) : isbn;
            }
            return SimpleKeyGenerator.generateKey(params);
        };
    }

    /**
     * 복합 파라미터 KeyGenerator
     * - 여러 파라미터를 조합하여 특정 형식의 키 생성
     */
    @Bean("compositeKeyGenerator")
    public KeyGenerator compositeKeyGenerator() {
        return (target, method, params) -> {
            // 파라미터가 2개일 때: "param0_param1" 형식
            if (params.length == 2) {
                return params[0] + "_" + params[1];
            }
            return SimpleKeyGenerator.generateKey(params);
        };
    }

    // =========================================================================
    // 📌 3. CacheResolver 설정
    // =========================================================================

    /**
     * 런타임 CacheResolver - 파라미터 값에 따라 다른 캐시 사용
     *
     * 💡 학습 포인트:
     * - CacheResolver는 런타임에 어떤 캐시를 사용할지 결정
     * - cacheNames와 달리 동적으로 캐시 선택 가능
     *
     * 예시:
     * - VIP 고객 요청: vipBooks 캐시 사용
     * - 일반 고객 요청: books 캐시 사용
     */
    @Bean("runtimeCacheResolver")
    public CacheResolver runtimeCacheResolver(
            @Qualifier("primaryCacheManager") CacheManager primary,
            @Qualifier("vipCacheManager") CacheManager vip) {
        return new CacheResolver() {
            @Override
            public Collection<? extends org.springframework.cache.Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
                Object[] args = context.getArgs();

                // 파라미터에 "VIP"가 포함되어 있으면 VIP 캐시 사용
                for (Object arg : args) {
                    if (arg instanceof String str && str.contains("VIP")) {
                        return Arrays.asList(vip.getCache("vipBooks"));
                    }
                }

                // 기본적으로 primary 캐시 사용
                return Arrays.asList(primary.getCache("advancedBooks"));
            }
        };
    }

    /**
     * 가격 기반 CacheResolver
     * - 책 가격에 따라 다른 캐시 사용 (프리미엄 vs 일반)
     */
    @Bean("priceTierCacheResolver")
    public CacheResolver priceTierCacheResolver(@Qualifier("primaryCacheManager") CacheManager primaryCacheManager) {
        return context -> {
            String methodName = context.getMethod().getName();

            // 메서드명에 "Premium"이 포함되면 customCache1 사용
            if (methodName.contains("Premium")) {
                return Arrays.asList(primaryCacheManager.getCache("advancedBooks"));
            }

            return Arrays.asList(primaryCacheManager.getCache("books"));
        };
    }

    // =========================================================================
    // 📌 4. CachingConfigurer 구현 - 전역 기본값 설정
    // =========================================================================

    /**
     * 기본 CacheManager 지정
     * - CachingConfigurer를 구현하여 전역 기본값 설정
     */
    @Override
    public CacheManager cacheManager() {
        return primaryCacheManager();
    }

    /**
     * 기본 KeyGenerator 지정
     * - keyGenerator를 명시하지 않으면 이 KeyGenerator 사용
     *
     * 💡 주의:
     * - 여기서는 null을 반환하여 Spring 기본(SimpleKeyGenerator) 사용
     * - 필요시 커스텀 KeyGenerator를 반환할 수 있음
     */
    @Override
    public KeyGenerator keyGenerator() {
        // 기본 SimpleKeyGenerator 사용 (null 반환 시 기본값 사용)
        return null;
    }

    /**
     * 기본 CacheResolver 지정
     * - cacheResolver를 명시하지 않으면 이 Resolver 사용
     */
    @Override
    public CacheResolver cacheResolver() {
        // SimpleCacheResolver 사용 (null 반환 시 기본값 사용)
        return null;
    }

    /**
     * 캐시 에러 핸들러
     * - 캐시 작업 중 에러 발생 시 처리 방법 정의
     *
     * 💡 학습 포인트:
     * - SimpleCacheErrorHandler: 예외를 그대로 던짐 (기본값)
     * - 커스텀 핸들러: 로깅만 하고 메서드 실행 계속 가능
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}
