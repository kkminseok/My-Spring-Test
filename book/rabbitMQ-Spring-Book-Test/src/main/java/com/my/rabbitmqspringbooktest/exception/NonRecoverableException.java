package com.my.rabbitmqspringbooktest.exception;

public class NonRecoverableException extends RuntimeException{

    public NonRecoverableException(String message, Throwable cause) {
        super(message,cause);
    }
}
