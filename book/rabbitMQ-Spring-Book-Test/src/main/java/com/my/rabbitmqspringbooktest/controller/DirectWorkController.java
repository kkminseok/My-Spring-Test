package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.ImageTaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectWorkController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/work/request-image-resize")
    public String requestImageResize(@RequestParam String fileName) {

        log.info("Sending image resize request: {}", fileName);


        for (int i = 0; i < 3; ++i) {
            String taskId = UUID.randomUUID().toString();
            int width = 1920 / (i+1);
            int height = 1080 / (i+1);

            // 1. 처리할 작업의 내용을 DTO객체로 정의
            ImageTaskDto task = new ImageTaskDto(taskId, fileName, width, height);

            // 2. 메시지 큐로 발행
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE_NAME,
                    RabbitMQConfig.DIRECT_ROUTING_KEY,
                    task);
        }

        return "Image resize request sent.";
    }
}
