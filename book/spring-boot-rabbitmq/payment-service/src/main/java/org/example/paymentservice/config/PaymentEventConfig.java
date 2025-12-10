package org.example.paymentservice.config;

import org.example.paymentservice.dto.OrderCreatedEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentEventConfig {

    public static final String EXCHANGE_NAME = "events.topic.exchange";

    // 구독자
    public static final String INBOUND_ROUTING_KEY = "order.created";
    public static final String PAYMENT_QUEUE_NAME = "payment.queue";

    // 발행자
    public static final String OUTBOUND_ROUTING_KEY_COMPLETED = "payment.completed";
    public static final String OUTBOUND_ROUTING_KEY_FAILED = "payment.failed";

    // DLQ
    public static final String DLX_NAME = "payment.dlx";
    public static final String DLQ_NAME = "payment.dlq";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE_NAME)
                .quorum()
                // 이 큐에서 처리 실패한 메시지는 payment.dlx로 보내도록
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .build();
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange exchange) {
        return BindingBuilder.bind(paymentQueue)
                .to(exchange)
                .with(INBOUND_ROUTING_KEY);
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
