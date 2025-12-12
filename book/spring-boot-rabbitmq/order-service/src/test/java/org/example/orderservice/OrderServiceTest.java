package org.example.orderservice;

import org.example.orderservice.config.OrderEventProducerConfig;
import org.example.orderservice.domain.Order;
import org.example.orderservice.domain.OrderStatus;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.dto.OrderCreatedEvent;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @DisplayName("주문 생성 요청이 유효할 경우, 주문을 저장하고 이벤트 발행")
    @Test
    void createOrder_whenGivenValidRequest_thenSavesOrderAndPublishesEvent() {
        // Given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                "kms-123",
                "product-123",
                2,
                new BigDecimal("100000")
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order createOrder = orderService.createOrder(createOrderRequest);

        // Then
        // 1번 호출되었는지 확인
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        // 객체 내용 확인
        Order savedOrder = orderCaptor.getValue();
        Assertions.assertEquals(savedOrder.getUserId(), createOrder.getUserId());
        Assertions.assertEquals(savedOrder.getStatus(), OrderStatus.PENDING);
        Assertions.assertNotNull(savedOrder.getOrderId());

        // 이벤트 발송이 1번 호출되었는지
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);

        verify(rabbitTemplate, times(1))
                .convertAndSend(
                        exchangeCaptor.capture(),
                        routingKeyCaptor.capture(),
                        eventCaptor.capture()
                );

        // 발행된 이벤트 내용 확인
        Assertions.assertEquals(exchangeCaptor.getValue(), OrderEventProducerConfig.EXCHANGE_NAME);
        Assertions.assertEquals(routingKeyCaptor.getValue(), OrderEventProducerConfig.ORDER_CREATED_ROUTING_KEY);;
        OrderCreatedEvent publishedEvent = eventCaptor.getValue();
        Assertions.assertEquals(publishedEvent.getOrderId(), savedOrder.getOrderId());
        Assertions.assertEquals(publishedEvent.getUserId(), savedOrder.getUserId());
        Assertions.assertNotNull(publishedEvent.getProductId());


    }
}
