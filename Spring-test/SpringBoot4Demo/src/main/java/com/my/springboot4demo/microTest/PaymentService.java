package com.my.springboot4demo.microTest;

import io.micrometer.observation.annotation.ObservationKeyValue;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    @Observed(name = "payment.process")
    @ObservationKeyValue(key = "payment.type", value = "card")
    @ObservationKeyValue(key = "payment.method", value = "online")
    public String processPayment(String paymentId) {
        log.info("Processing payment: {}", paymentId);
        return "processed:" + paymentId;
    }

    @Observed(name = "payment.validate")
    @ObservationKeyValue(key = "validation.type", value = "strict")
    public boolean validatePayment(String paymentId) {
        log.info("Validating payment: {}", paymentId);
        return paymentId != null && !paymentId.isEmpty();
    }
}
