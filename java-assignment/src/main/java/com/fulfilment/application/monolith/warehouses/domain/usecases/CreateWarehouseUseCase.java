package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.WarehouseValidationException;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  @Inject
  WarehouseStore warehouseStore;

  @Inject
  LocationResolver locationResolver;

  @Override
  public void create(Warehouse warehouse) {

    validateBusinessUnitCode(warehouse);

    Location location =
            locationResolver.resolveByIdentifier(
                    warehouse.location);

    validateLocationCapacity(location, warehouse);

    validateLocationWarehouseLimit(location, warehouse);

    warehouseStore.create(warehouse);
  }

  private void validateBusinessUnitCode(Warehouse warehouse) {

    if (warehouseStore.findByBusinessUnitCode(
            warehouse.businessUnitCode) != null) {

      throw new WarehouseValidationException(
              "Business unit code already exists");
    }
  }

  private void validateLocationCapacity(
          Location location,
          Warehouse warehouse) {

    if (warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
              "Warehouse capacity exceeds location limit");
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new WarehouseValidationException(
              "Stock cannot exceed capacity");
    }
  }

  private void validateLocationWarehouseLimit(
          Location location,
          Warehouse warehouse) {

    long activeWarehousesAtLocation =
            warehouseStore.getAll().stream()
                    .filter(w -> warehouse.location.equals(w.location))
                    .filter(w -> w.archivedAt == null)
                    .count();

    if (activeWarehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
              "Maximum number of warehouses reached for location "
                      + location.identification);
    }
  }
}