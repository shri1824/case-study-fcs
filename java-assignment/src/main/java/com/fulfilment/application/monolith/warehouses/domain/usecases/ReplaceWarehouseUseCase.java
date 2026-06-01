package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {


  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  @Inject
  public ReplaceWarehouseUseCase(
          WarehouseStore warehouseStore,
          LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {

    Warehouse current =
            warehouseStore.findByBusinessUnitCode(
                    newWarehouse.businessUnitCode);

    if (current == null) {
      throw new WarehouseNotFoundException(
              "Warehouse not found");
    }

    validateReplacement(current, newWarehouse);

    Location location =
            locationResolver.resolveByIdentifier(
                    newWarehouse.location);

    validateLocationCapacity(location, newWarehouse);

    current.archivedAt = LocalDateTime.now();

    warehouseStore.update(current);

    warehouseStore.create(newWarehouse);
  }

  private void validateReplacement(
          Warehouse current,
          Warehouse replacement) {

    if (replacement.stock == null
            || !replacement.stock.equals(current.stock)) {

      throw new WarehouseValidationException(
              "Replacement warehouse stock must match existing warehouse stock");
    }

    if (replacement.capacity < current.stock) {

      throw new WarehouseValidationException(
              "Replacement warehouse capacity cannot accommodate existing stock");
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
}