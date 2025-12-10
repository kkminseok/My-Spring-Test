package org.example.stockservice.config;

import org.example.stockservice.dto.PaymentCompletedEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/*
    * Stock Event Configuration
    * 결제 서비스에서 결제 이벤트를 수신하여 처리관련 이벤트를 발행하는 설정 클래스
 */
@Configuration
public class StockEventConfig {

    public static final String EXCHANGE_NAME = "events.topic.exchange";

    public static final String INBOUND_ROUTING_KEY = "payment.completed";
    public static final String STOCK_QUEUE_NAME = "stock.queue";

    public static final String DLX_NAME = "stock.dlx";
    public static final String DLQ_NAME = "stock.dlq";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue stockQueue() {
        return QueueBuilder.durable(STOCK_QUEUE_NAME)
                .quorum()
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .build();
    }

    @Bean
    public Binding stockBinding(Queue stockQueue, TopicExchange exchange) {
        return BindingBuilder.bind(stockQueue)
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


        idClassMapping.put("org.example.paymentservice.dto.PaymentCompletedEvent", PaymentCompletedEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}
