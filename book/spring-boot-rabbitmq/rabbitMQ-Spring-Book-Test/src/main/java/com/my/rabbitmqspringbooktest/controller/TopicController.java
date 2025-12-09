package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.LogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TopicController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/logs")
    public String publishLog(@RequestBody LogDto logDto) {
        //1. 수신한 데이터 보강
        logDto.setTimestamp(LocalDateTime.now());

        //2. 동적 라우팅 키 생성
        String routingKey = String.format("logs.%s.%s", logDto.getLevel().toLowerCase(), logDto.getCountry().toLowerCase());

        //3. 토픽 익스체인지로 메시지 발행
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE_NAME,
                routingKey,
                logDto
        );

        log.info("Published log message: {} with routing key: {}", logDto, routingKey);

        return "Log message published successfully.";



    }
}
