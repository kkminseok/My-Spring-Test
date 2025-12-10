package org.example.orderservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.config.OrderEventProducerConfig;
import org.example.orderservice.domain.Order;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 주문 서비스 클래스
 * Outbox 패턴을 활용하여 주문 생성 및 재고 확인 요청을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {
        //1. 주문 객체 생성 및 DB 저장 (트랜잭션)
        Order newOrder = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .userId(createOrderRequest.getUserId())
                .productId(createOrderRequest.getProductId())
                .quantity(createOrderRequest.getQuantity())
                .price(createOrderRequest.getPrice())
                .build();

        orderRepository.save(newOrder);
        log.info("Order created and saved to DB: {}", newOrder.getOrderId());

        // 2. 다른 서비스에 알림 이벤트 객체 생성
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                newOrder.getOrderId(),
                newOrder.getUserId(),
                newOrder.getProductId(),
                newOrder.getQuantity(),
                newOrder.getPrice(),
                newOrder.getOrderTimestamp()
        );

        // 3. RabbitMQ 이벤트 발행
        try {
            log.info("Order created and sent to RabbitMQ: {}", orderCreatedEvent);
            rabbitTemplate.convertAndSend(
                    OrderEventProducerConfig.EXCHANGE_NAME,
                    OrderEventProducerConfig.ORDER_CREATED_ROUTING_KEY,
                    orderCreatedEvent
            );
        } catch (Exception e) {
            log.error("Order created and sent to RabbitMQ: {}", orderCreatedEvent, e);
            // 이 시점에서는 이미 디비 커밋이 진행되었을 가능성이 큼. 롤백이 불가능하기에 보상 트랜잭션을 보내야함.
            throw new RuntimeException("Order created and sent to RabbitMQ: " + orderCreatedEvent, e);
        }

        return newOrder;
    }


}
