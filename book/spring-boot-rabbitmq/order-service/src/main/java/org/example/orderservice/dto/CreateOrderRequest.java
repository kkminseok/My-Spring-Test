package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateOrderRequest {
    private String userId;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
}
