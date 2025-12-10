package org.example.stockservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.stockservice.config.StockEventConfig;
import org.example.stockservice.dto.PaymentCompletedEvent;
import org.example.stockservice.dto.StockDeductionFailedEvent;
import org.example.stockservice.service.IdempotencyService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockConsumer {

    private final IdempotencyService idempotencyService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = StockEventConfig.STOCK_QUEUE_NAME)
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        // 1. 멱등성 보장
        if (!idempotencyService.startProcessing(event.getEventId())) {
            log.warn("DUPLICATE EVENT RECEIVED: {}", event.getEventId());
            // 이미 처리된 메시지, ACK를 보내서 큐에서 안전하게 제거
            return;
        }

        try {
            // 2. 핵심 비즈니스 로직
            log.info("Decreasing stock for order: {}, product: {}", event.getOrderId(), event.getProductId());
            //대충 재고 줄이는 로직

            // 3. 멱등성 처리 완료
            idempotencyService.setCompleted(event.getEventId());
            log.info("Stock decrease COMPLETED for event orderId: {}", event.getOrderId());
        } catch (Exception e) {
            // 비즈니스 로직 실패 에러라고 치고
            log.error("INSUFFICIENT STOCK for order: {}. Publishing compensation event.", event.getOrderId(), e);

            // 보상 트랜잭션 실행 이 이벤트를 받은 결제 서비스는 환불을 진행해야함.
            publishStockDeductionFailedEvent(event, e.getMessage());

            // 비즈니스 예외는 성공적으로 '처리'된 것이므로, 멱등성 키 변경
            idempotencyService.setCompleted(event.getEventId());

        } catch (Throwable t) {
            //시스템 장애 등으로 인한 실패
            log.error("SYSTEM ERROR while processing stock deduction for order: {}", event.getOrderId(), t);
            //예외를 던져서 메시지가 큐에 남아있도록 함.
            throw t;
        }
    }

    // 재고 차감 실패 보상 이벤트 발생
    private void publishStockDeductionFailedEvent(PaymentCompletedEvent event, String reason) {
        StockDeductionFailedEvent stockDeductionFailedEvent = new StockDeductionFailedEvent(
                UUID.randomUUID().toString(),
                event.getOrderId(),
                reason,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(StockEventConfig.EXCHANGE_NAME,
                "stock.deduction.failed",
                stockDeductionFailedEvent
                );
        log.info("StockDeductionFailedEvent published: {}", stockDeductionFailedEvent);
    }
}
