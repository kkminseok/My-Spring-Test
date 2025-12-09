package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.dto.CreateOrderRequest;
import com.my.rabbitmqspringbooktest.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderProducerController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/orders")
    public String createOrder(@RequestBody CreateOrderRequest createOrderRequest) {

        // 1. 클라이언트가 멱등성 키를 보냈는지 감지
        if(createOrderRequest.getIdempotencyKey() == null) {
            throw new IllegalArgumentException("idempotencyKey is null");
        }
        log.info("Received order creation request: {}", createOrderRequest.getIdempotencyKey());

        // 2. 메시지 큐로 보낼 DTO를 생성
        OrderDto orderDto = new OrderDto(
                createOrderRequest.getIdempotencyKey(),
                generateOrderId(),
                createOrderRequest.getProductName(),
                createOrderRequest.getQuantity()
        );

        // 3. 메시지 큐로 주문 생성 요청 전송
        rabbitTemplate.convertAndSend(
                "test.order.exchange",
                "order.create",
                orderDto
        );

        log.info("Order creation request sent to RabbitMQ: {}", orderDto.getIdempotencyKey());

        // 4. 클라이언트에 응답 반환
        return "Order request accepted. Idempotency Key: " + orderDto.getIdempotencyKey();
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }
}
