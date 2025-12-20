package com.my.springbootmaxmessagediskmonitoring;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class LargeMessageProducer {

    private static final int MESSAGE_SIZE_BYTES = 4 * 1024 * 500; // ~2MB
    private static final int MESSAGE_COUNT = 1000;

    private final RabbitTemplate rabbitTemplate;

    public void sendBulk() {
        // ⚠️ payload는 한 번만 생성 (1000번 생성 ❌)
        byte[] payload = new byte[MESSAGE_SIZE_BYTES];
        Arrays.fill(payload, (byte) 'A');

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            rabbitTemplate.convertAndSend(
                    "exchange.classic",
                    "routing.classic",
                    payload
            );

            if (i % 100 == 0) {
                System.out.println("Sent " + i + " messages");
            }
        }

        System.out.println("Finished sending " + MESSAGE_COUNT + " messages");
    }
}