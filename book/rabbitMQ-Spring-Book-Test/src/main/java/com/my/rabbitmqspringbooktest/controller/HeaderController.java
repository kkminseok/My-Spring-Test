package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HeaderController {

    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    @PostMapping("/notifications")
    public String sendNotification(
            @RequestBody NotificationDto notificationDto,
            @RequestParam String channel,
            @RequestParam(required = false) Boolean isBetaTester
    ) {
        log.info("Sending notification: {} via channel: {}, isBetaTester: {}", notificationDto, channel, isBetaTester);

        //1. 메시지 메타데이터를 담을 객체 생성
        MessageProperties messageProperties = new MessageProperties();

        //2. 요청 파라미터를 기반으로 헤더를 동적으로 생성
        messageProperties.setHeader("channel", channel);
        if (Boolean.TRUE.equals(isBetaTester)) {
            messageProperties.setHeader("beta-tester", "true");
        }

        if ("high".equalsIgnoreCase(notificationDto.getPriority())) {
            messageProperties.setHeader("priority", "high");
        }

        //3. 메시지 본문과 헤더를 하나의 AMQP Message 객체로 만듦
        Message message = messageConverter.toMessage(notificationDto, messageProperties);

        // 4. 헤더 교환기로 메시지 발송
        rabbitTemplate.send(
                RabbitMQConfig.HEADERS_EXCHANGE_NAME,
                "",
                message
        );

        return "Notification sent successfully via headers exchange.";

    }
}
