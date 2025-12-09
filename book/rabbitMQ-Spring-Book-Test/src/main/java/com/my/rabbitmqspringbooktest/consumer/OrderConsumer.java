package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.dto.OrderDto;
import com.my.rabbitmqspringbooktest.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final IdempotencyService idempotencyService;

    @RabbitListener(queues = "order.create.queue")
    public void createOrder(OrderDto orderDto) {
        // 1. 처리시작 및 중복 검사
        if(!idempotencyService.startProcessing(orderDto.getIdempotencyKey().toString())) {
            log.warn("DUPLICATE message detected. Skipping processing for Idempotency Key: {}", orderDto.getIdempotencyKey());
            //이미 처리된 메시지이므로 아무것도 하지 않고 정상 종료
            return ;
        }

        try {
            //2. 멱등성 검사를 통과한 유일한 실행임을 보장
            log.info("Processing order: {}", orderDto);
            // DB set 했다고 치고
            Thread.sleep(1000);

            //3. 처리 완료 상태로 업데이트
            idempotencyService.setCompleted(orderDto.getIdempotencyKey().toString());
            log.info("Order processed successfully: key{}", orderDto.getIdempotencyKey().toString());
        } catch (Exception e) {
            //4.처리 실패시 롤백
            log.error("Error while processing order: {}", orderDto, e);
            // 예외를 던져서 NACK를 보내도록 함. DLQ로 이동시키거나 재시도 시킴
            throw new RuntimeException("Error while processing order: " + orderDto, e);
        }
    }
}
