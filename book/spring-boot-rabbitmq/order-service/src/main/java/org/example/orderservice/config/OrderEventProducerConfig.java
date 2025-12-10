package org.example.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
public class OrderEventProducerConfig {

    public static final String EXCHANGE_NAME = "events.topic.exchange";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        // Producer -> Exchange 도착 보장
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(ack) {
                log.info("✅ Message sent to exchange successfully, correlationData: {}", correlationData);
            } else {
                log.error("❗️ Failed to send message to exchange, correlationData: {}, cause: {}", correlationData, cause);

                // DB에 발행 실패 상태를 기록하거나 재발행 로직 수행
            }
        });

        // Exchange -> Queue 라우팅 보장
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Message Returned (Unroutable). \n ~ Message: {} \n ~ ReplyCode: {} \n ~ ReplyText: {} \n ~ Exchange: {} \n ~ RoutingKey: {}",
                    returned.getMessage(),
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey());
            // 실패시 개발자에게 알림 보내기 조치
        });
        return rabbitTemplate;
    }
}
