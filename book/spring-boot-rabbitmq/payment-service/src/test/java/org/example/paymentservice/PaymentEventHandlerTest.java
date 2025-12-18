package org.example.paymentservice;

import org.assertj.core.api.Assertions;
import org.example.paymentservice.config.PaymentEventConfig;
import org.example.paymentservice.consumer.PaymentEventHandler;
import org.example.paymentservice.dto.OrderCreatedEvent;
import org.example.paymentservice.dto.PaymentCompletedEvent;
import org.example.paymentservice.dto.PaymentFailedEvent;
import org.example.paymentservice.service.IdempotencyService;
import org.example.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentEventHandler 테스트")
class PaymentEventHandlerTest {

    @InjectMocks
    private PaymentEventHandler paymentEventHandler;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PaymentService paymentService;

    private OrderCreatedEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "order-123",
                "user-123",
                "product-123",
                1,
                new BigDecimal(11000),
                LocalDateTime.now()
        );
    }

    @DisplayName("새로운 이벤트 수신 시, 결제를 처리하고 성공 이벤트를 발행")
    @Test
    void whenEventIsNew_thenProcessPaymentAndPublishesSuccessEvent() {
        // Given
        when(idempotencyService.startProcessing(sampleEvent.getIdempotencyKey())).thenReturn(true);

        // When
        paymentEventHandler.handleOrderCreatedEvent(sampleEvent);

        // Then
        // 1번 호출 검증
        verify(paymentService, times(1)).processPayment(sampleEvent);

        // 멱등성 테스트 서비스 상태가 COMPLETED 되었는지
        verify(idempotencyService, times(1)).setCompleted(sampleEvent.getIdempotencyKey());

        // 결제 성공 이벤트가 올바른 내용으로 발행되었는지
        ArgumentCaptor<PaymentCompletedEvent> paymentCompletedEventArgumentCaptor = ArgumentCaptor.forClass(PaymentCompletedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(PaymentEventConfig.EXCHANGE_NAME),
                eq(PaymentEventConfig.OUTBOUND_ROUTING_KEY_COMPLETED),
                paymentCompletedEventArgumentCaptor.capture()
        );

        PaymentCompletedEvent publishEvent = paymentCompletedEventArgumentCaptor.getValue();
        Assertions.assertThat(publishEvent.getOrderId()).isEqualTo(sampleEvent.getOrderId());
    }

    @DisplayName("이미 처리된 중복 이벤트 수신 시, 모든 처리를 건너 뛴다.")
    @Test
    void whenEventIsDuplicate_thenSkipsAllProcessing() {
        // Given
        // 멱등성 체크 시, 이미 처리된 이벤트임을 설정
        when(idempotencyService.startProcessing(sampleEvent.getIdempotencyKey())).thenReturn(false);

        // When
        paymentEventHandler.handleOrderCreatedEvent(sampleEvent);

        // Then
        verify(paymentService, never()).processPayment(any());
        verify(idempotencyService, never()).setCompleted(any());
        verify(rabbitTemplate, never()).convertAndSend((Object) any(), any(), any());
    }

    @DisplayName("결제 처리 중 비즈니스 예외 발생 시, 실패 이벤트 발행")
    @Test
    void whenPaymentFails_thenPublishesFailedEvent() {
        // Given
        when(idempotencyService.startProcessing(sampleEvent.getIdempotencyKey())).thenReturn(true);
        when(paymentService.processPayment(sampleEvent))
                .thenThrow(new IllegalStateException("Credit card limit exceeded"));
        // When
        paymentEventHandler.handleOrderCreatedEvent(sampleEvent);

        // Then
        //1. '성공' 이벤트 미발행
        verify(rabbitTemplate, never()).convertAndSend((Object) any(), any(), any());

        //2. '실패' 이벤트 발행 검증
        ArgumentCaptor<PaymentFailedEvent> paymentFailedEventArgumentCaptor = ArgumentCaptor.forClass(PaymentFailedEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(PaymentEventConfig.EXCHANGE_NAME),
                eq(PaymentEventConfig.OUTBOUND_ROUTING_KEY_FAILED),
                paymentFailedEventArgumentCaptor.capture()
        );

        PaymentFailedEvent publishedEvent = paymentFailedEventArgumentCaptor.getValue();
        Assertions.assertThat(publishedEvent.getOrderId()).isEqualTo(sampleEvent.getOrderId());
        Assertions.assertThat(publishedEvent.getReason()).isEqualTo("Credit card limit exceeded");

        // 실패시에도 멱등성상태는 'completed'여야 함
        verify(idempotencyService, times(1)).setCompleted(sampleEvent.getIdempotencyKey());
    }

    @Test
    void test() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        LocalDateTime nowPlus3Min = now.plusMinutes(3);
        System.out.println(nowPlus3Min);

        System.out.println(now.isBefore(nowPlus3Min));

    }
}
