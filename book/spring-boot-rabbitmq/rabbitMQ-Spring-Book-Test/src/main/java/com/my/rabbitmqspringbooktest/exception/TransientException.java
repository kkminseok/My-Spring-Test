package com.my.rabbitmqspringbooktest.exception;

public class TransientException extends RuntimeException {
    public TransientException(String message) {
        super(message);
    }
}
