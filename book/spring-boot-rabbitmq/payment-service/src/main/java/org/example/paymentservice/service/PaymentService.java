package org.example.paymentservice.service;


import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.dto.OrderCreatedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentService {

    public boolean processPayment(OrderCreatedEvent orderCreatedEvent) {
        // 결제 처리 로직 (예: 결제 게이트웨이 연동)
        log.info("processPayment({})", orderCreatedEvent);
        // 실제 결제 로직은 여기에 구현
        return true;
    }
}
