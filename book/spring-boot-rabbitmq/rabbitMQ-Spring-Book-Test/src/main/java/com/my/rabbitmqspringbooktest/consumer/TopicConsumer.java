package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.LogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicConsumer {

    @RabbitListener(queues = RabbitMQConfig.ALL_LOGS_QUEUE)
    public void consumeAllLogs(LogDto logDto) {
        log.info("All Logs Consumer received message: {}", logDto);
        // 모든 로그 처리 로직 구현
    }

    @RabbitListener(queues = RabbitMQConfig.ERROR_LOGS_QUEUE)
    public void consumeErrorLogs(LogDto logDto) {
        log.info("Error Logs Consumer received message: {}", logDto);
        // 에러 로그 처리 로직 구현
    }

    @RabbitListener(queues = RabbitMQConfig.KOREAN_LOGS_QUEUE)
    public void consumeKoreanLogs(LogDto logDto) {
        log.info("Korean Logs Consumer received message: {}", logDto);
        // 한국 관련 로그 처리 로직 구현
    }
}
