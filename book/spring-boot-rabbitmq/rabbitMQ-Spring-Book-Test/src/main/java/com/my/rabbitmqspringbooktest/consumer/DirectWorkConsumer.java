package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.ImageTaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DirectWorkConsumer {

    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE_NAME)
    public void processImageTask(ImageTaskDto task) throws InterruptedException {

        log.info("Worker [{}] started processing task: {}",
                Thread.currentThread().getName(), task);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Task processing was interrupted: {}", task, e);
            throw e;
        }

        log.info("Worker [{}] finished processing task: {}",
                Thread.currentThread().getName(), task);


    }
}
