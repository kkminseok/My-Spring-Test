package org.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String idempotencyKey;
    private String orderId;
    private String userId;
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime orderTimestamp;
}
