package org.example.paymentservice;

import org.assertj.core.api.Assertions;
import org.example.paymentservice.config.PaymentEventConfig;
import org.example.paymentservice.config.TestConfig;
import org.example.paymentservice.dto.OrderCreatedEvent;
import org.example.paymentservice.dto.PaymentCompletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Import(TestConfig.class)
@DisplayName("결제 이벤트 핸들러 통합 테스트")
public class PaymentEventHandlerIntegrationTest {

    // static으로 선언시에 이 클래스 내의 모든 테스트 메서드가 '하나의' 컨테이너를 공유하게 됨. 최적화.
    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12.1-management-alpine"));


    @DynamicPropertySource
    static void setRabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host",rabbit::getHost);
        registry.add("spring.rabbitmq.port",rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.listener.simple.default-requeue-rejected", ()-> false);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin; // RabbitMQ 큐 등 관리

    @DisplayName("OrderCreatedEvent 수신 시, 결제를 처리하고 PaymentCompletedEvent 발행")
    @Test
    void whenOrderCreatedEventIsReceived_thenPaymentCompletedEventShouldBePublished() {
        // Given
        // 테스트 큐를 만들고 바인딩
        String testListenerQueue = "test.payment.completed.queue";
        rabbitAdmin.declareQueue(new Queue(testListenerQueue, true));
        rabbitAdmin.declareBinding(new Binding(testListenerQueue, Binding.DestinationType.QUEUE,
                PaymentEventConfig.EXCHANGE_NAME,
                PaymentEventConfig.OUTBOUND_ROUTING_KEY_COMPLETED, null));

        // 테스트용 이벤트 객체
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(UUID.randomUUID().toString(), "order-123", "user-123",
                "product-123", 1, new BigDecimal(11000), LocalDateTime.now());

        // When
        rabbitTemplate.convertAndSend(
                PaymentEventConfig.EXCHANGE_NAME,
                PaymentEventConfig.INBOUND_ROUTING_KEY,
                orderCreatedEvent
        );

        // Then
        // Awaitility 패턴으로 비동기 이벤트 수신 대기
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            PaymentCompletedEvent paymentCompletedEvent = (PaymentCompletedEvent) rabbitTemplate.receiveAndConvert(testListenerQueue);

            Assertions.assertThat(paymentCompletedEvent).isNotNull();
            Assertions.assertThat(paymentCompletedEvent.getOrderId()).isEqualTo(orderCreatedEvent.getOrderId());
        });

        rabbitAdmin.deleteQueue(testListenerQueue);

    }


}
