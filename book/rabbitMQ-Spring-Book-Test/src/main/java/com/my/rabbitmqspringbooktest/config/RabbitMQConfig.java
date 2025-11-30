package com.my.rabbitmqspringbooktest.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "hello.exchange";
    public static final String QUEUE_NAME = "hello.queue";
    public static final String ROUTING_KEY = "hello.key.#";

    public static final String DIRECT_EXCHANGE_NAME = "direct.exchange";
    public static final String DIRECT_QUEUE_NAME = "direct.queue";
    public static final String DIRECT_ROUTING_KEY = "direct.key";

    public static final String FANOUT_EXCHANGE_NAME = "user.events.exchange";

    public static final String EMAIL_QUEUE_NAME = "user.deactivated.email.queue";
    public static final String AUTH_QUEUE_NAME = "user.deactivated.auth.queue";
    public static final String FEED_QUEUE_NAME = "user.deactivated.feed.queue";

    public static final String TOPIC_EXCHANGE_NAME = "logs.topic.exchange";
    public static final String ALL_LOGS_QUEUE="logs.all.queue";
    public static final String ERROR_LOGS_QUEUE="logs.error.queue";
    public static final String KOREAN_LOGS_QUEUE="logs.korea.queue";

    public static final String BIDING_PATTERN_ALL="logs.#";
    public static final String BIDING_PATTERN_ERROR="logs.error.*";
    public static final String BINDING_PATTERN_KOREA="logs.*.korea";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    // --- Subscriber Queues ---

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE_NAME);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE_NAME);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE_NAME);
    }

    @Bean
    public Queue feedQueue() {
        return new Queue(FEED_QUEUE_NAME);
    }

    @Bean
    public Queue allLogsQueue() {
        return new Queue(ALL_LOGS_QUEUE);
    }

    @Bean
    public Queue errorLogsQueue() {
        return new Queue(ERROR_LOGS_QUEUE);
    }

    @Bean
    public Queue koreanLogsQueue() {
        return new Queue(KOREAN_LOGS_QUEUE);
    }

    // --- Subscriber Queues ---


    // --- Bindings ----

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding directBinding(Queue directQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueue).to(directExchange).with(DIRECT_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(emailQueue).to(fanoutExchange);
    }

    @Bean
    public Binding authBinding(Queue authQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(authQueue).to(fanoutExchange);
    }

    @Bean
    public Binding feedBinding(Queue feedQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(feedQueue).to(fanoutExchange);
    }

    @Bean
    public Binding allLogsBinding(Queue allLogsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(allLogsQueue)
                .to(topicExchange)
                .with(BIDING_PATTERN_ALL);
    }

    @Bean
    public Binding errorLogsBinding(Queue errorLogsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(errorLogsQueue)
                .to(topicExchange)
                .with(BIDING_PATTERN_ERROR);
    }

    @Bean
    public Binding koreanLogsBinding(Queue koreanLogsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(koreanLogsQueue)
                .to(topicExchange)
                .with(BINDING_PATTERN_KOREA);
    }



    // --- Bindings ----

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
