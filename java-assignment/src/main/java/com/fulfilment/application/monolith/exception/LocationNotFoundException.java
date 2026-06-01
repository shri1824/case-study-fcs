package com.fulfilment.application.monolith.exception;

public class LocationNotFoundException extends RuntimeException {
  public LocationNotFoundException(String message) {
    super(message);
  }

}
