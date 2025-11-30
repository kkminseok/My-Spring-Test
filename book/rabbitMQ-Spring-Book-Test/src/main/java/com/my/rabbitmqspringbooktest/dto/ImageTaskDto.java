package com.my.rabbitmqspringbooktest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageTaskDto {
    private String taskId;
    private String originalFileName;
    private int targetWidth;
    private int targetHeight;
}
