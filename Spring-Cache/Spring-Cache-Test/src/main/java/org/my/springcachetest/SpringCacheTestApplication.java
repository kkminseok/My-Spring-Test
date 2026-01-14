package org.my.springcachetest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // 캐시 기능 활성화 - 이 어노테이션이 없으면 @Cacheable 등이 동작하지 않음
public class SpringCacheTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheTestApplication.class, args);
    }

}
