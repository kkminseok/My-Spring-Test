package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeaderConsumer {



    @RabbitListener(queues = RabbitMQConfig.SMS_NOTIFICATION_QUEUE)
    public void consumeSmsNotification(NotificationDto notificationDto) {
        log.info("Received SMS Notification Message : {}", notificationDto);
    }

    @RabbitListener(queues = RabbitMQConfig.KAKAO_NOTIFICATION_QUEUE)
    public void consumeKakaoNotification(NotificationDto notificationDto) {
        log.info("Received Kakao Notification Message : {}", notificationDto);
    }
}
