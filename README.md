# Baeldung & Spring 학습 테스트 코드 저장소

[Baeldung](https://www.baeldung.com/) 튜토리얼과 Spring 공식 예제를 기반으로 작성한 학습용 테스트 코드 모음입니다.

> 블로그에서 자세한 설명을 확인할 수 있습니다: https://ms727.tistory.com/

---

## 목차

- [Java Basic](#java-basic)
- [Java String](#java-string)
- [Java Web](#java-web)
- [Java Test](#java-test)
- [Kotlin](#kotlin)
- [Spring Test](#spring-test)
- [Spring Messaging](#spring-messaging)
- [Spring WebSocket](#spring-websocket)
- [Spring Reactive](#spring-reactive)
- [Spring Security](#spring-security)
- [Spring Data](#spring-data)
- [Spring Cache](#spring-cache)
- [Spring Observability](#spring-observability)
- [Spring Thumbnailator](#spring-thumbnailator)
- [My Test (개인 학습)](#my-test-개인-학습)
- [Book (도서 예제)](#book-도서-예제)
- [Quarkus](#quarkus)

---

## Java Basic

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Pass-By-Value as a Parameter Passing Mechanism | [Baeldung](https://www.baeldung.com/java-pass-by-value-or-pass-by-reference) | [PassByValueTest.java](Java-basic/src/test/java/basic/PassByValueTest.java) |
| Varargs in Java | [Baeldung](https://www.baeldung.com/java-varargs) | [VarargsTest.java](Java-basic/src/test/java/basic/VarargsTest.java) |
| Guide to hashCode() in Java | [Baeldung](https://www.baeldung.com/java-hashcode#handling-hash-collisions) | [HashCodeTest.java](Java-basic/src/test/java/basic/HashCodeTest.java) |

---

## Java String

### Basics

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| String Initialization in Java | [Baeldung](https://www.baeldung.com/java-string-initialization) | [StringBasicInitTest.java](Java-String/src/test/java/basics/StringBasicInitTest.java) |
| Why String Is Immutable in Java? | [Baeldung](https://www.baeldung.com/java-string-immutable) | [StringImmutableTest.java](Java-String/src/test/java/basics/StringImmutableTest.java) |
| Concatenating Strings in Java | [Baeldung](https://www.baeldung.com/java-strings-concatenation) | [StringConcatenationTest.java](Java-String/src/test/java/basics/StringConcatenationTest.java) |
| Guide to Java String Pool | [Baeldung](https://www.baeldung.com/java-string-pool#string-interning) | [StringPoolTest.java](Java-String/src/test/java/basics/StringPoolTest.java) |
| How to Iterate Over the String Characters | [Baeldung](https://www.baeldung.com/java-iterate-string-characters) | [StringLoopTest.java](Java-String/src/test/java/basics/StringLoopTest.java) |
| Comparing Strings in Java | [Baeldung](https://www.baeldung.com/java-compare-strings) | [StringCompareTest.java](Java-String/src/test/java/basics/StringCompareTest.java) |
| Guide to Character Encoding | [Baeldung](https://www.baeldung.com/java-char-encoding) | [StringEncodingTest.java](Java-String/src/test/java/basics/StringEncodingTest.java) |
| Java Multi-line String | [Baeldung](https://www.baeldung.com/java-multiline-string) | [StringMultiLineTest.java](Java-String/src/test/java/basics/StringMultiLineTest.java) |
| Java Text Blocks | [Baeldung](https://www.baeldung.com/java-text-blocks) | [StringJava15MultiLineTest.java](Java-String/src/test/java/basics/StringJava15MultiLineTest.java) |
| String Interpolation in Java | [Baeldung](https://www.baeldung.com/java-string-interpolation) | [StringInterpolationTest.java](Java-String/src/test/java/basics/StringInterpolationTest.java) |

### Basic Manipulations

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Checking for Empty or Blank Strings | [Baeldung](https://www.baeldung.com/java-blank-empty-strings) | [CheckEmptyOrBlankTest.java](Java-String/src/test/java/manipulations/CheckEmptyOrBlankTest.java) |
| Split a String in Java | [Baeldung](https://www.baeldung.com/java-split-string) | [SplitSimplyTest.java](Java-String/src/test/java/manipulations/SplitSimplyTest.java) |
| Adding a Newline Character to a String | [Baeldung](https://www.baeldung.com/java-string-newline) | [NewLineTest.java](Java-String/src/test/java/manipulations/NewLineTest.java) |
| How to Remove the Last Character of a String? | [Baeldung](https://www.baeldung.com/java-remove-last-character-of-string) | [RemoveLastCharTest.java](Java-String/src/test/java/manipulations/RemoveLastCharTest.java) |
| Check If a String Is Numeric | [Baeldung](https://www.baeldung.com/java-check-string-number) | [CheckStringNumberTest.java](Java-String/src/test/java/manipulations/CheckStringNumberTest.java) |
| Check If a String Is a Valid Date | [Baeldung](https://www.baeldung.com/java-string-valid-date) | [CheckDateValidatorTest.java](Java-String/src/test/java/manipulations/CheckDateValidatorTest.java) |
| Capitalize the First Letter of a String | [Baeldung](https://www.baeldung.com/java-string-uppercase-first-letter) | [CapitalizeFirstLetterTest.java](Java-String/src/test/java/manipulations/CapitalizeFirstLetterTest.java) |
| Remove Whitespace From a String | [Baeldung](https://www.baeldung.com/java-string-remove-whitespace) | [RemoveWhitespaceTest.java](Java-String/src/test/java/manipulations/RemoveWhitespaceTest.java) |
| String Concatenation in Java | [Baeldung](https://www.baeldung.com/java-string-concatenation) | [StringConcatenationTest.java](Java-String/src/test/java/manipulations/StringConcatenationTest.java) |
| Convert a Comma Separated String to a List | [Baeldung](https://www.baeldung.com/java-string-with-separator-to-list) | [ConnvertCommaSeperatedStringTest.java](Java-String/src/test/java/manipulations/ConnvertCommaSeperatedStringTest.java) |
| Difference Between isEmpty() and isBlank() | [Baeldung](https://www.baeldung.com/java-string-isempty-vs-isblank) | [IsBlankIsEmptyTest.java](Java-String/src/test/java/manipulations/IsBlankIsEmptyTest.java) |

### Advanced Manipulations

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Java – Generate Random String | [Baeldung](https://www.baeldung.com/java-random-string) | [GenerateRandomStringTest.java](Java-String/src/test/java/manipulations/advance/GenerateRandomStringTest.java) |
| Count Occurrences of a Char in a String | [Baeldung](https://www.baeldung.com/java-count-chars) | [CountCharTest.java](Java-String/src/test/java/manipulations/advance/CountCharTest.java) |
| Check if a String Is a Palindrome | [Baeldung](https://www.baeldung.com/java-palindrome) | [CheckPalindromeTest.java](Java-String/src/test/java/manipulations/advance/CheckPalindromeTest.java) |
| Check if Two Strings Are Anagrams | [Baeldung](https://www.baeldung.com/java-strings-anagrams) | [AnagramTest.java](Java-String/src/test/java/manipulations/advance/AnagramTest.java) |

---

## Java Web

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| A Simple HTTP Server With Java ServerSocket | [Baeldung](https://www.baeldung.com/java-serversocket-simple-http-server) | [SimpleHttpServerMain.java](Java-web/src/main/java/com/my/socket/server/SimpleHttpServerMain.java) |

---

## Java Test

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Test a REST API with Java | [Baeldung](https://www.baeldung.com/integration-testing-a-rest-api) | [RestAPITest.java](Java-Test/src/test/java/RestAPITest.java) |

---

## Kotlin

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Kotlin Tutorial | - | [Kotlin-Tutorial](Kotlin-test/Kotlin-Tutorial) |

---

## Spring Test

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Avoid Brittle Tests for the Service Layer | [Baeldung](https://www.baeldung.com/testing-the-java-service-layer#templates) | [Spring-test](Spring-test/) |

---

## Spring Messaging

### Kafka

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring Kafka 공식 예제 1, 2 + 커스텀 | [Sample-01](https://github.com/spring-projects/spring-kafka/tree/main/samples/sample-01) | [Spring-Boot-Kafka-Producer](Spring-messaging/Spring-Boot-Kafka-Producer) |
| Spring Kafka Batch Sample | [Sample-03](https://github.com/spring-projects/spring-kafka/tree/main/samples/sample-03) | [Spring-Boot-Kafka-Batch-Sample](Spring-messaging/Spring-Boot-Kafka-Batch-Sample) |
| Spring Kafka Retry Sample | [Sample-04](https://github.com/spring-projects/spring-kafka/tree/main/samples/sample-04) | [Spring-Boot-Kafka-Retry-Sample](Spring-messaging/Spring-Boot-Kafka-Retry-Sample) |
| Spring Kafka Embedded Sample | [Sample-05](https://github.com/spring-projects/spring-kafka/tree/main/samples/sample-05) | [Spring-Boot-Kafka-Embedded-Sample](Spring-messaging/Spring-Boot-Kafka-Embedded-Sample) |
| Spring Kafka Topology Test Sample | [Sample-06](https://github.com/spring-projects/spring-kafka/tree/main/samples/sample-06) | [Spring-Boot-Kafka-TopologyTest-Sample](Spring-messaging/Spring-Boot-Kafka-TopologyTest-Sample) |
| Spring Kafka KIP-848 Test | [Sample-07](https://github.com/spring-projects/spring-kafka/blob/main/samples/sample-07/README.adoc) | [Spring-Boot-Kafka-KIP-848-Test](Spring-messaging/Spring-Boot-Kafka-KIP-848-Test) |
| Spring Kafka Micrometer Sample | [Sample-08](https://github.com/spring-projects/spring-kafka/blob/main/samples/sample-08/README.adoc) | [Spring-Boot-Kafka-Micrometer-Sample](Spring-messaging/Spring-Boot-Kafka-Micrometer-Sample) |
| Kafka Micrometer + Log4j2 연동 테스트 | - | [Spring-Boot-Kafka-Micrometer-Sample-Log4j2](Spring-messaging/Spring-Boot-Kafka-Micrometer-Sample-Log4j2) |

### RabbitMQ

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring RabbitMQ STOMP Sample | - | [Spring-RabbitMQ-STOMP-Sample](Spring-messaging/Spring-RabbitMQ-STOMP-Sample) |

---

## Spring WebSocket

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring STOMP 예제 | [Spring Guide](https://spring.io/guides/gs/messaging-stomp-websocket) | [Spring-STOMP-example](Spring-WebSocket/Spring-STOMP-example) |

---

## Spring Reactive

### WebFlux

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Guide to Spring WebFlux | [Baeldung](https://www.baeldung.com/spring-webflux) | [Spring-Webflux-Guide](Spring-reactive/Spring-Webflux-Guide) |
| Spring WebFlux Filters | [Baeldung](https://www.baeldung.com/spring-webflux-filters) | [ExampleWebFilterTest.java](Spring-reactive/Spring-Webflux-Filter/src/test/java/com/my/springwebfluxfilter/ExampleWebFilterTest.java) |
| Static Content in Spring WebFlux | [Baeldung](https://www.baeldung.com/spring-webflux-static-content) | [RoutingExample.java](Spring-reactive/Spring-Webflux-static-content/src/main/java/com/my/springwebfluxstaticcontent/RoutingExample.java) |
| Handling Errors in Spring WebFlux | [Baeldung](https://www.baeldung.com/spring-webflux-errors) | [Spring-Webflux-Error-handling](Spring-reactive/Spring-Webflux-Error-handling/src/main/java/com/my/springwebfluxerrorhandling/) |
| How to Return 404 with Spring WebFlux | [Baeldung](https://www.baeldung.com/spring-webflux-404) | [Spring-Webflux-HttpStatus](Spring-reactive/Spring-Webflux-HttpStatus/src/main/java/com/my/springwebfluxhttpstatus/) |
| Spring MVC Async vs Spring WebFlux | [Baeldung](https://www.baeldung.com/spring-mvc-async-vs-webflux) | [Spring-Webflux-AsyncDiffTest](Spring-reactive/Spring-Webflux-AsyncDiffTest/src/main/java/com/my/springwebfluxasyncdifftest/) |
| Difference Between Flux and Mono | [Baeldung](https://www.baeldung.com/java-reactor-flux-vs-mono) | [Spring-Webflux-mono-flux-test](Spring-reactive/Spring-Webflux-mono-flux-test/src/test/java/com/my/springwebfluxmonofluxtest/) |

### WebClient

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring WebClient | [Baeldung](https://www.baeldung.com/spring-5-webclient) | [Spring-Webflux-WebClient](Spring-reactive/Spring-Webflux-WebClient/src/test/java/com/my/springwebfluxwebclient/) |
| Spring WebClient vs. RestTemplate | [Baeldung](https://www.baeldung.com/spring-webclient-resttemplate) | [SpringRestTemplateCompareWebClient](Spring-reactive/SpringRestTemplateCompareWebClient/) |
| Spring WebClient Requests with Parameters | [Baeldung](https://www.baeldung.com/webflux-webclient-parameters) | [Spring-Webflux-WebClient-With-Param](Spring-reactive/Spring-Webflux-WebClient-With-Param/src/test/java/com/my/springwebfluxwebclientwithparam/) |
| Spring WebClient Filters | [Baeldung](https://www.baeldung.com/spring-webclient-filters) | [SpringWebfluxWebclientFilterApplicationTests.java](Spring-reactive/Spring-Webflux-Webclient-Filter/src/test/java/com/my/springwebfluxwebclientfilter/SpringWebfluxWebclientFilterApplicationTests.java) |

---

## Spring Security

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring Boot + KeyCloak 테스트 | - | [Spring-Keycloak-Sample](Spring-Security/Spring-Keycloak-Sample) |

---

## Spring Data

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring Data JPA | - | [Spring-Data-JPA](Spring-Data/Spring-Data-JPA) |
| Spring Data ElasticSearch | - | [Spring-Data-ElasticSearch](Spring-Data/Spring-Data-ElasticSearch) |

---

## Spring Cache

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring Cache Test | - | [Spring-Cache-Test](Spring-Cache/Spring-Cache-Test) |

---

## Spring Observability

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Spring Micrometer Test | - | [Spring-Micrometer-Test](Spring-Observability/Spring-Micrometer-Test) |
| Spring OpenTelemetry Test | - | [Spring-OpenTelemetry-Test](Spring-Observability/Spring-OpenTelemetry-Test) |

---

## Spring Thumbnailator

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Thumbnailator 이미지 처리 테스트 | [Thumbnailator GitHub](https://github.com/coobird/thumbnailator) | [Spring-Thumbnailator-Test](Spring-Thumbnailator-Test/) |

---

## My Test (개인 학습)

| 주제 | 설명 | 코드 |
|------|------|------|
| Collections Test | Java Collections 학습 | [CollectionsTest](My-test/CollectionsTest) |
| EnumMap Test | EnumMap 사용법 학습 | [EnumMapTest](My-test/EnumMapTest) |
| Java Test Quality Test | 테스트 품질 관련 학습 | [JavaTestQualityTest](My-test/JavaTestQualityTest) |
| ScopedValue Test | Java ScopedValue 학습 | [ScopeValueTest](My-test/ScopeValueTest) |
| Spring WebFlux Mono Test | Mono 심화 테스트 | [Spring-Webflux-Mono-Test](My-test/Spring-Webflux-Mono-Test) |
| Virtual Thread Test (Java 24) | Virtual Thread 학습 | [VirtualThreadTest-24](My-test/VirtualThreadTest-24) |

---

## Book (도서 예제)

| 도서명 | 설명 | 코드 |
|--------|------|------|
| Modern Java in Action | 모던 자바 인 액션 예제 코드 | [modern-java-in-action](book/modern-java-in-action) |
| Spring Boot RabbitMQ | RabbitMQ 도서 예제 | [spring-boot-rabbitmq](book/spring-boot-rabbitmq) |

---

## Quarkus

| 주제 | 참고 링크 | 코드 |
|------|----------|------|
| Quarkus Hello World | [Quarkus](https://quarkus.io/) | [quarkus-hello-world](quarkus/quarkus-hello-world) |
