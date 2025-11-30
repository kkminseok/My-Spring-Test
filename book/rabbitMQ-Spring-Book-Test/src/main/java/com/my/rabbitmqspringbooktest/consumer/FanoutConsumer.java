package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.UserEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FanoutConsumer {

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE_NAME)
    public void handleEmailNotification(UserEventDto userEventDto) {
        log.info("Email Service received message: {}", userEventDto);
        // 이메일 전송 로직 구현
    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_NAME)
    public void handleAuthServiceNotification(UserEventDto userEventDto) {
        log.info("Auth Service received message: {}", userEventDto);
        // 인증 서비스 관련 로직 구현
    }

    @RabbitListener(queues = RabbitMQConfig.FEED_QUEUE_NAME)
    public void handleFeedServiceNotification(UserEventDto userEventDto) {
        log.info("Feed Service received message: {}", userEventDto);
        // 피드 서비스 관련 로직 구현
    }
}
