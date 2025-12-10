package org.example.notificationservice.config;

import org.example.notificationservice.dto.OrderCreatedEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NotificationEventConfig {

    public static final String EXCHANGE_NAME = "events.topic.exchange";

    public static final String NOTIFICATION_QUEUE_NAME = "notification.queue";
    public static final String INBOUND_BINDING_PATTERN = "*.created";

    public static final String DLX_NAME = "notification.dlx";
    public static final String DLQ_NAME = "notification.dlq";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .quorum()
                .build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(exchange)
                .with(INBOUND_BINDING_PATTERN);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DLX_NAME);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DLQ_NAME);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange);
    }


    @Bean
    public MessageConverter messageConverter() {
        JacksonJsonMessageConverter jacksonJsonMessageConverter = new JacksonJsonMessageConverter();
        jacksonJsonMessageConverter.setClassMapper(classMapper());
        return jacksonJsonMessageConverter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();


        idClassMapping.put("org.example.orderservice.dto.OrderCreatedEvent", OrderCreatedEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

}
