package com.my.rabbitmqspringbooktest.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ReliablePublishingConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        /**
         * 1. Publisher Confirms 활성화
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("😀Message successfully delivered to exchange. CorrelationData: {}", correlationData);
            } else {
                log.error("🐄Failed to deliver message to exchange. CorrelationData: {}, cause: {}", correlationData, cause);
                // 재시도 로직 또는 대체 처리 로직 구현 가능
            }
        });
        /**
         * 2. Publisher Returns 활성화
         */
        rabbitTemplate.setMandatory(true); // Return 콜백을 활성화하려면 mandatory 플
        rabbitTemplate.setReturnsCallback(returned -> {
            // 라우팅 실패. Exchange에는 도달했으나, 큐에 라우팅되지 못한 경우
            log.error("??Message returned: replyCode={}, replyText={}, exchange={}, routingKey={}, message={}",
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getMessage());
        });

        return rabbitTemplate;

    }
}
