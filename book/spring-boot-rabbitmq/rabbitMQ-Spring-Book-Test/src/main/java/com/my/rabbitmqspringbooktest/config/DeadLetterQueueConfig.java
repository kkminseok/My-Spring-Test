package com.my.rabbitmqspringbooktest.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeadLetterQueueConfig {

    // 1. Main Work Exchange & Queue
    public static final String WORK_EXCHANGE = "work.exchange";
    public static final String WORK_QUEUE = "work.queue";
    public static final String WORK_ROUTING_KEY = "work.key";

    // 2. Dead Letter Exchange & Queue
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.exchange";
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead.letter.key";

    @Bean
    public DirectExchange workExchange() {
        return new DirectExchange(WORK_EXCHANGE);
    }


    @Bean
    public Queue workQueue() {
        return QueueBuilder.durable(WORK_QUEUE)
                .withArgument("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key",DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding workBinding(Queue workQueue, DirectExchange workExchange) {
        return BindingBuilder.bind(workQueue)
                .to(workExchange)
                .with(WORK_ROUTING_KEY);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange);
    }

}
