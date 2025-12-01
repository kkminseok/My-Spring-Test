package com.my.springboot4demo;

import com.my.springboot4demo.microTest.PaymentService;
import com.my.springboot4demo.microTest.TestObservationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ObservationKeyValueTest {


//    @Autowired
//    private PaymentService paymentService;
//
//    @Autowired
//    private TestObservationConfig observationHandler;
//
//    @BeforeEach
//    void setUp() {
//        observationHandler.clear();
//    }
//
//
//    @Test
//    void shouldCaptureObservationWithKeyValues() {
//        // when
//        String result = paymentService.processPayment("test-123");
//
//        // then
//        assertThat(observationHandler.getCapturedContexts()).isNotEmpty();
//        var context = observationHandler.getCapturedContexts().get(0);
//        assertThat(context.getName()).isEqualTo("payment.process");
//
//        var keyValues = context.getAllKeyValues();
//        System.out.println("Captured KeyValues: " + keyValues);
//
//        assertThat(keyValues).hasSize(4);
//
//        // 실제 동작에 맞게 수정
//        assertThat(keyValues).anySatisfy(kv -> {
//            System.out.println("Checking KeyValue - Key: " + kv.getKey() + ", Value: " + kv.getValue());
//            assertThat(kv.getKey()).isEqualTo("card");
//            assertThat(kv.getValue()).isEqualTo("processed:test-123");
//        });
//        assertThat(keyValues).anySatisfy(kv -> {
//            assertThat(kv.getKey()).isEqualTo("online");
//            assertThat(kv.getValue()).isEqualTo("processed:test-123");
//        });
//
//        // 기본 메타데이터도 확인
//        assertThat(keyValues).anySatisfy(kv -> {
//            assertThat(kv.getKey()).isEqualTo("class");
//            assertThat(kv.getValue()).isEqualTo("com.my.springboot4demo.microTest.PaymentService");
//        });
//        assertThat(keyValues).anySatisfy(kv -> {
//            assertThat(kv.getKey()).isEqualTo("method");
//            assertThat(kv.getValue()).isEqualTo("processPayment");
//        });
//    }
}