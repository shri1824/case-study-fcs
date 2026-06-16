package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;


@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  @Inject WarehouseStore warehouseStore;

  @Override
  public void archive(Warehouse warehouse) {

    if (warehouse == null) {
      throw new WarehouseValidationException("Warehouse cannot be null");
    }

    if (warehouse.archivedAt != null) {
      throw new WarehouseValidationException("Warehouse already archived");
    }

    // mark archived
    warehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(warehouse);
  }
}