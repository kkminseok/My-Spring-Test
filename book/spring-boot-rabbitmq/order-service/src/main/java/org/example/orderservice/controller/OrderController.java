package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.domain.Order;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.dto.CreateOrderResponse;
import org.example.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        Order newOrder = orderService.createOrder(createOrderRequest);
        CreateOrderResponse response = new CreateOrderResponse(newOrder.getOrderId(),
                "Order created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
