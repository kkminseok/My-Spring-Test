package com.my.rabbitmqspringbooktest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LogDto {
    private String level;
    private String country;
    private String message;
    private LocalDateTime timestamp;
}
