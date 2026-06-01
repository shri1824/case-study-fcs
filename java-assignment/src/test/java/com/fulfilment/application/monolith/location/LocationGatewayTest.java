package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.exception.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertEquals("ZWOLLE-001", location.identification);
  }

  @Test
  public void testWhenResolveNonExistingLocationShouldThrowException() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    LocationNotFoundException exception =
            assertThrows(
                    LocationNotFoundException.class,
                    () -> locationGateway.resolveByIdentifier("UNKNOWN-001"));

    // then
    assertEquals(
            "Location with identifier UNKNOWN-001 not found",
            exception.getMessage());
  }

  @Test
  public void testWhenIdentifierIsNullShouldThrowException() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> locationGateway.resolveByIdentifier(null));

    // then
    assertEquals("Identifier cannot be null or blank", exception.getMessage());
  }

  @Test
  public void testWhenIdentifierIsBlankShouldThrowException() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    IllegalArgumentException exception =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> locationGateway.resolveByIdentifier(" "));

    // then
    assertEquals("Identifier cannot be null or blank", exception.getMessage());
  }
}