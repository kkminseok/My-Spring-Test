package org.example.paymentservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.config.PaymentEventConfig;
import org.example.paymentservice.dto.OrderCreatedEvent;
import org.example.paymentservice.dto.PaymentCompletedEvent;
import org.example.paymentservice.dto.PaymentFailedEvent;
import org.example.paymentservice.service.IdempotencyService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final IdempotencyService idempotencyService;
    private final RabbitTemplate rabbitTemplate;

    // order.created 이벤트 처리 메서드 예시
    @RabbitListener(queues = PaymentEventConfig.PAYMENT_QUEUE_NAME)
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        //1. 멱등성 보장
        if(!idempotencyService.startProcessing(orderCreatedEvent.getIdempotencyKey())) {
            log.warn("DUPLICATE EVENT RECEIVED: {}", orderCreatedEvent.getIdempotencyKey());
            //이미 처리된 메시지
            return ;
        }

        try {
            // 비즈니스 로직 수행
            log.info("Processing OrderCreatedEvent: {}", orderCreatedEvent);
            processPayment(orderCreatedEvent);

            // 성공 이벤트 발행
            publishPaymentSuccessEvent(orderCreatedEvent);

            // 멱등성 처리 완료
            idempotencyService.setCompleted(orderCreatedEvent.getIdempotencyKey());
            log.info("Payment processing COmPLETED for event: {}", orderCreatedEvent.getIdempotencyKey());
        } catch (Exception e) {
            //실패 처리 및 이벤트 발행
            log.error("Payment processing FAILED for event: {}", orderCreatedEvent.getIdempotencyKey(), e);
            publishPaymentFailedEvent(orderCreatedEvent, e.getMessage());

            // 실패시에도 처리는 완료된 것이므로 멱등성 키를 완료 상태로 설정
            idempotencyService.setCompleted(orderCreatedEvent.getIdempotencyKey());

            //비즈니스 로직 실패는 재시도할 필요가 없으므로 예외를 다시 던지지 않음.
        }
    }

    private void processPayment(OrderCreatedEvent orderCreatedEvent) throws InterruptedException {
        log.info("Calling external PG API for order {}...", orderCreatedEvent.getIdempotencyKey());

        Thread.sleep(1000);// 뭐 처리했다고 치고

        // 특정 물품은 실패한다고 가정
        if("PRODUCT-FAIL-001".equalsIgnoreCase(orderCreatedEvent.getProductId())) {
            throw new IllegalStateException("Simulated payment failure for testing.");
        }
    }

    private void publishPaymentSuccessEvent(OrderCreatedEvent sourceEvent) {
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                sourceEvent.getOrderId(),
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                PaymentEventConfig.EXCHANGE_NAME,
                PaymentEventConfig.OUTBOUND_ROUTING_KEY_COMPLETED,
                paymentCompletedEvent
        );

        log.info("Payment processing COMPLETED for event: {}", paymentCompletedEvent);
    }

    private void publishPaymentFailedEvent(OrderCreatedEvent sourceEvent, String reason) {
        PaymentFailedEvent failEvent = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                sourceEvent.getOrderId(),
                reason,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                PaymentEventConfig.EXCHANGE_NAME,
                PaymentEventConfig.OUTBOUND_ROUTING_KEY_FAILED,
                failEvent
        );
        log.info("Payment processing FAILED for event: {}", failEvent);
    }
}
