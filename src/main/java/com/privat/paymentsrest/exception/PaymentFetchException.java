package com.privat.paymentsrest.exception;

public class PaymentFetchException extends RuntimeException {
    public PaymentFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentFetchException(String message) {
    }
}