package com.my.rabbitmqspringbooktest.service;

import com.my.rabbitmqspringbooktest.dto.ImageTaskDto;
import com.my.rabbitmqspringbooktest.exception.NonRecoverableException;
import com.my.rabbitmqspringbooktest.exception.TransientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkService {

    public void processImageTask(ImageTaskDto imageTaskDto) {
        log.info("Business logic started for task: {}", imageTaskDto.toString());

        //1. 일시적인 오류 시뮬레이션
        if("transient-error.jpg".equalsIgnoreCase(imageTaskDto.getOriginalFileName())) {
            log.warn("Simulating a transient error for task: {}", imageTaskDto.getTaskId());
            throw new TransientException("Simulating a transient error for task: " + imageTaskDto.getTaskId());
        }

        //2. 복구 불가능한(영구적인) 오류 시뮬레이션
        if("non-recoverable-error.jpg".equalsIgnoreCase(imageTaskDto.getOriginalFileName())) {
            log.error("Simulating a non-recoverable error for task: {}", imageTaskDto.getTaskId());
            throw new NonRecoverableException("Simulating a non-recoverable error for task: " + imageTaskDto.getTaskId());
        }

        //3. 성공
        log.info("Processing image task successfully: {}", imageTaskDto.getTaskId());
    }
}
