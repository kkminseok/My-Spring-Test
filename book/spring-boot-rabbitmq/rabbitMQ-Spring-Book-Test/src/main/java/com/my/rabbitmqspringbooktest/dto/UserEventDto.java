package com.my.rabbitmqspringbooktest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto {

    private String userId;
    private String eventType; // DEACTIVATED, CREATED
    private LocalDateTime evenetTimestamp;
}
