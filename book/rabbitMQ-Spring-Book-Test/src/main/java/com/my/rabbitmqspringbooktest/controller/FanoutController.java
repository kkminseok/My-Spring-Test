package com.my.rabbitmqspringbooktest.controller;

import com.my.rabbitmqspringbooktest.config.RabbitMQConfig;
import com.my.rabbitmqspringbooktest.dto.UserEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FanoutController {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/users/{userId}/deactivate")
    public String deactivateUser(@PathVariable String userId) {
        log.info("Deactivating user with ID: {}", userId);

        // 디비에서 사용자를 비활성화 상태로 바꿨다고 치고

        // 사용자 비활성화 이벤트를 발행
        UserEventDto userEvent = new UserEventDto(
                userId,
                "DEACTIVATED",
                LocalDateTime.now()
        );

        // fanout 익스체인지로 메시지 발행
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.FANOUT_EXCHANGE_NAME,
                "", // fanout 익스체인지에는 라우팅 키가 필요 없음
                userEvent
        );

        log.info("Published user deactivation event: {}", userEvent);

        // 응답 반환
        return "User with ID " + userId + " has been deactivated.";
    }

}
