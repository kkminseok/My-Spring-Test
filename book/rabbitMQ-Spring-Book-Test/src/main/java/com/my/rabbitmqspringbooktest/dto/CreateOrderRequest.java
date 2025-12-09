package com.my.rabbitmqspringbooktest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {
    private String idempotencyKey;
    private String productName;
    private int quantity;
}

