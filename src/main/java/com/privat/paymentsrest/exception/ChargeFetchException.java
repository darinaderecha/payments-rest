package com.privat.paymentsrest.exception;

public class ChargeFetchException extends RuntimeException {
  public ChargeFetchException(String message) {
    super(message);
  }

  public ChargeFetchException(String message, Throwable cause) {
    super(message, cause);
  }
}
