package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
                "hello.key.1",
                message);

        return "Message sent: " + message;
    }
}
