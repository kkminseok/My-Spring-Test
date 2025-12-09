package com.my.rabbitmqspringbooktest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;
@Data
@AllArgsConstructor
public class OrderDto {
    private String idempotencyKey;
    private String orderId;
    private String productName;
    private int quantity;
}
