package com.privat.paymentsrest.exception;


public class PaymentCreationException extends RuntimeException {
    public PaymentCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentCreationException(String message) {
    }
}


