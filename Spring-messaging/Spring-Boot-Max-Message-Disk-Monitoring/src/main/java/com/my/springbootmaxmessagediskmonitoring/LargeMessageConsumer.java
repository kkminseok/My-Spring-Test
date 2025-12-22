package com.my.springbootmaxmessagediskmonitoring;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LargeMessageConsumer {

    @RabbitListener(queues = "queue.classic")
    //@RabbitListener(queues = "stock.queue")
    public void consume(byte[] message) {
        System.out.println("Received message size = " + message.length + " bytes");
    }

    @RabbitListener(queues = "queue.quorum")
    public void consumeQuorumQueue(byte[] message) {
        System.out.println("Received message from quorum queue, size = " + message.length + " bytes");
    }
}