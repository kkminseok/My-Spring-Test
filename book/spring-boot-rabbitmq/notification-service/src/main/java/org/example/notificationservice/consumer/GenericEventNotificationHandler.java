package org.example.notificationservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.config.NotificationEventConfig;
import org.example.notificationservice.dto.OrderCreatedEvent;
import org.example.notificationservice.dto.UserCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitListener(queues = NotificationEventConfig.NOTIFICATION_QUEUE_NAME)
public class GenericEventNotificationHandler {


    /**
     * OrderCreateEvent Type의 알림 처리 메서드
     */
    @RabbitHandler
    public void handleOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        log.info("[Notification Handler | Order Created] Sending notification for orderId: {}", orderCreatedEvent.getOrderId());

        // 사용자에게 주문접수 이메일/푸시 알림 발송 로직
    }

    /**
     * UserCreated 같은 이벤트 (이런건 아직 없음.)
     */
    @RabbitHandler
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("[Notification Handler | User Created] Sending welcome notification for userId: {}", event.getUserId());

        // 사용자에게 환영 이메일/푸시 알림 발송 로직 + 쿠폰 제공 등..
    }

    /**
     * 폴백 핸들러: 처리되지 않은 이벤트 타입에 대한 알림 처리 메서드
     */
    @RabbitHandler(isDefault = true)
    public void handleUnknownEvent(Object event) {
        log.warn("[Notification Handler | Unknown Event] Received unknown event type: {}", event.getClass().getName());

        //큐에서 제거 필요시 디비나 별도 이벤트성 로그 기록
    }

}
