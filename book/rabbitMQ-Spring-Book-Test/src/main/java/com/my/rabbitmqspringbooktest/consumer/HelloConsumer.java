package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.exception.NonRecoverableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message){
        try {
            log.info("Received message: {}", message);


            throw new NonRecoverableException("Simulated non-recoverable error",null);
        } catch (NonRecoverableException e) {
            log.error("Non-recoverable error occurred: {}", e.getMessage());

            // NACK the message without requeuing
            throw new AmqpRejectAndDontRequeueException("Non-recoverable error occurred",e);
        }
        // NACK requeue
        //throw new RuntimeException("NonRecoverableException");
    }
}
