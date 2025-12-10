package org.example.stockservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDeductionFailedEvent {
    private String eventId;
    private String orderId;
    private String reason;
    private LocalDateTime failedTimestamp;
}
