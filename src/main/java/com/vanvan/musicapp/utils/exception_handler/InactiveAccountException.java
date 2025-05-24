package com.vanvan.musicapp.utils.exception_handler;

public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException(String message) {
        super(message);
    }
}