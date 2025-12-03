package com.my.rabbitmqspringbooktest.consumer;

import com.my.rabbitmqspringbooktest.config.DeadLetterQueueConfig;
import com.my.rabbitmqspringbooktest.dto.ImageTaskDto;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterConsumer {

    public static final int MAX_RETRY_COUNT = 3;
    public final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = DeadLetterQueueConfig.WORK_QUEUE)
    public void processImageTask(ImageTaskDto dto) {
        log.info("work started processing task: {}", dto);
        if("error-image.jpg".equalsIgnoreCase(dto.getOriginalFileName())) {
            log.error("work error image file");
            //재시도 불가능한 부분이므로 에러 발생
            throw new IllegalArgumentException("Invalid Image format that cannot be processed.");
        }

        try {
            //정상
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("work finished processing task: {}", dto);
    }

    @RabbitListener(queues = DeadLetterQueueConfig.DEAD_LETTER_QUEUE, ackMode = "MANUAL")
    public void handleDeadLetter(Message deadLetter, Channel channel) {
        log.error("!!! Dead Letter Received: {} \n info: {}", deadLetter.getBody(),
                deadLetter.getMessageProperties().getHeaders());

        //후속 조치

        long deliveryTag = deadLetter.getMessageProperties().getDeliveryTag();
        String messageBody = new String(deadLetter.getBody());
        log.info("Reprocessing message from DLQ. DeliveryTag: {}, Body: {}", deliveryTag, messageBody);

        try {
            long retryCount = 0;
            if(retryCount < MAX_RETRY_COUNT) {
                log.warn("Retrying to process message from DLQ. RetryCount: {}", retryCount+1);
                rabbitTemplate.send(
                        DeadLetterQueueConfig.WORK_EXCHANGE,
                        DeadLetterQueueConfig.WORK_ROUTING_KEY,
                        deadLetter
                );
            } else {
                // 최대 재시도 횟수 초과 시 별도 처리 로직 (예: 알림, 로그 기록 등)
                log.error("Retrying to process message from DLQ. RetryCount: {}", retryCount);
                rabbitTemplate.send("parking-lot-exchange",
                        "parking.lot.routing.key",
                        deadLetter
                );

                channel.basicAck(deliveryTag, false);
            }
        }catch (Exception e) {
            log.error("Error reprocessing message from DLQ: {}", e.getMessage(), e);

            try {

                // NACK the message and requeue it for another attempt
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("Error reprocessing message from DLQ: {}", ioException.getMessage(), ioException);
            }

        }


    }

}
