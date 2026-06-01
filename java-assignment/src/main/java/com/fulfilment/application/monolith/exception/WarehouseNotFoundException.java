package com.fulfilment.application.monolith.exception;

public class WarehouseNotFoundException extends RuntimeException {
  public WarehouseNotFoundException(String message) {
    super(message);
  }

}
