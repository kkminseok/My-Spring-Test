package com.my.springbootmaxmessagediskmonitoring;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LargeMessageConsumer {

    @RabbitListener(queues = "queue.classic")
    public void consume(byte[] message) {
        System.out.println("Received message size = " + message.length + " bytes");
    }
}