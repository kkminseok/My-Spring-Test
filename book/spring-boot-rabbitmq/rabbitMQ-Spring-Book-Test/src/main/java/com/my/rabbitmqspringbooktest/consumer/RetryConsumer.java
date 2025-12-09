package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.DeadLetterQueueConfig;
import com.my.rabbitmqspringbooktest.dto.ImageTaskDto;
import com.my.rabbitmqspringbooktest.exception.TransientException;
import com.my.rabbitmqspringbooktest.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryConsumer {

    private final WorkService workService;

    @Retryable(
            value = {TransientException.class} ,
            maxRetries = 3,
            delay = 1000,
            multiplier = 2.0
    )
    @RabbitListener(queues = DeadLetterQueueConfig.WORK_QUEUE)
    public void processImageTask(ImageTaskDto dto) {
            log.info("[Attempt] Processing image task: {}", dto.getTaskId());
            workService.processImageTask(dto);
    }







}
