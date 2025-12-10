package org.example.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCompletedEvent {
    private String eventId;
    private  String productId;
    private String orderId;
    private LocalDateTime paymentTimestamp;
}
