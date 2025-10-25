package com.my.springboot4demo;

import com.my.springboot4demo.JacksonTest.PaymentData;
import com.my.springboot4demo.JacksonTest.PaymentRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class Jackson3Test {

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJackson3Features() throws Exception {
        // Jackson 3.0 버전 확인
        System.out.println("Jackson version: " + objectMapper.version());
        assertThat(objectMapper.version().getMajorVersion()).isEqualTo(3);

        // Record 직렬화 테스트
        PaymentRecord payment = new PaymentRecord("test-123", "CARD", 1000);
        String json = objectMapper.writeValueAsString(payment);
        PaymentRecord deserialized = objectMapper.readValue(json, PaymentRecord.class);

        assertThat(deserialized).isEqualTo(payment);
        System.out.println("Record serialization: " + json);
    }

    @Test
    void testNewAnnotations() throws Exception {
        // Jackson 3.0의 새로운 기능 테스트
        PaymentData payment = new PaymentData();
        payment.setId("test-123");
        payment.setSensitiveData("secret");

        String json = objectMapper.writeValueAsString(payment);
        System.out.println("Serialized with new annotations: " + json);

        // sensitiveData는 직렬화에서 제외되어야 함
        assertThat(json).doesNotContain("secret");
        assertThat(json).contains("test-123");
    }
}
