package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.exception.LocationNotFoundException;
import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import jakarta.ws.rs.core.Response.Status;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;

import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOGGER =
          Logger.getLogger(WarehouseResourceImpl.class);

  @Inject
  WarehouseStore warehouseStore;

  @Inject
  CreateWarehouseOperation createWarehouseOperation;

  @Inject
  ArchiveWarehouseOperation archiveWarehouseOperation;

  @Inject
  ReplaceWarehouseOperation replaceWarehouseOperation;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {

    LOGGER.debug("Fetching all warehouse units");

    List<com.warehouse.api.beans.Warehouse> warehouses =
            warehouseStore.getAll()
                    .stream()
                    .filter(w -> w.archivedAt == null)
                    .map(this::toWarehouseResponse)
                    .toList();

    LOGGER.debugf(
            "Fetched %d warehouse units",
            warehouses.size());

    return warehouses;
  }

  @Override
  public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(
          @NotNull com.warehouse.api.beans.Warehouse data) {

    LOGGER.infof(
            "Creating warehouse. businessUnitCode=%s",
            data.getBusinessUnitCode());

    Warehouse warehouse = fromRequest(data);
    warehouse.createdAt = LocalDateTime.now();

    try {

      createWarehouseOperation.create(warehouse);

      LOGGER.infof(
              "Warehouse created successfully. businessUnitCode=%s",
              warehouse.businessUnitCode);

    } catch (LocationNotFoundException
             | WarehouseValidationException e) {

      LOGGER.warnf(
              "Warehouse creation failed. businessUnitCode=%s, reason=%s",
              data.getBusinessUnitCode(),
              e.getMessage());

      throw new WebApplicationException(
              e.getMessage(),
              400);
    }

    return toWarehouseResponse(warehouse);
  }
  @Override
  public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {

    Long warehouseId;

    try {
      warehouseId = Long.valueOf(id);
    } catch (NumberFormatException e) {
      throw new WebApplicationException(
              "Invalid warehouse id",
              400);
    }

    var warehouse =
            warehouseStore.findWarehouseById(warehouseId);

    if (warehouse == null) {
      throw new WebApplicationException(
              "Warehouse not found",
              404);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {

    Long warehouseId;

    try {
      warehouseId = Long.valueOf(id);
    } catch (NumberFormatException e) {
      throw new WebApplicationException(
              "Invalid warehouse id",
              400);
    }

    Warehouse warehouse =
            warehouseStore.findWarehouseById(warehouseId);

    if (warehouse == null) {
      throw new WebApplicationException(
              "Warehouse not found",
              404);
    }

    try {

      archiveWarehouseOperation.archive(warehouse);

    } catch (WarehouseValidationException e) {

      throw new WebApplicationException(
              e.getMessage(),
              400);
    }
  }

  @Override
  public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(
          String businessUnitCode,
          @NotNull com.warehouse.api.beans.Warehouse data) {

    LOGGER.infof(
            "Replacing warehouse. businessUnitCode=%s",
            businessUnitCode);
    Warehouse replacement = fromRequest(data);
    replacement.businessUnitCode = businessUnitCode;
    replacement.createdAt = LocalDateTime.now();

    try {

      replaceWarehouseOperation.replace(replacement);
      LOGGER.infof(
              "Warehouse replaced successfully. businessUnitCode=%s",
              businessUnitCode);

    } catch (WarehouseNotFoundException e) {
      LOGGER.warnf(
              "Warehouse replacement failed. businessUnitCode=%s, reason=%s",
              businessUnitCode,
              e.getMessage());
      throw new WebApplicationException(
              e.getMessage(),
              404);

    } catch (WarehouseValidationException e) {
      LOGGER.warnf(
              "Warehouse validation failed. businessUnitCode=%s, reason=%s",
              businessUnitCode,
              e.getMessage());

      throw new WebApplicationException(
              e.getMessage(),
              400);
    }
    return toWarehouseResponse(replacement);
  }
  private Warehouse findWarehouseOrThrow(
          String businessUnitCode) {
    Warehouse warehouse =
            warehouseStore.findByBusinessUnitCode(
                    businessUnitCode);
    if (warehouse == null) {

      LOGGER.warnf(
              "Warehouse not found. businessUnitCode=%s",
              businessUnitCode);

      throw new WebApplicationException(
              "Warehouse not found",
              Status.NOT_FOUND);
    }
    return warehouse;
  }

  private Warehouse fromRequest(
          com.warehouse.api.beans.Warehouse data) {

    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = data.getBusinessUnitCode();
    warehouse.location = data.getLocation();
    warehouse.capacity = data.getCapacity();
    warehouse.stock = data.getStock();

    return warehouse;
  }

  private com.warehouse.api.beans.Warehouse toWarehouseResponse(
          com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {

    com.warehouse.api.beans.Warehouse response = new com.warehouse.api.beans.Warehouse();

    if (warehouse.id != null) {
      response.setId(String.valueOf(warehouse.id));
    }

    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }
}