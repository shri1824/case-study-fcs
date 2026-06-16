package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {

    DbWarehouse db = new DbWarehouse();

    db.businessUnitCode = warehouse.businessUnitCode;
    db.location = warehouse.location;
    db.capacity = warehouse.capacity;
    db.stock = warehouse.stock;
    db.createdAt = warehouse.createdAt;
    db.archivedAt = warehouse.archivedAt;

    persist(db);
    warehouse.id = db.id; // important
  }

  @Override
  @Transactional
  public void update(Warehouse warehouse) {

    DbWarehouse entity =
            find("businessUnitCode", warehouse.businessUnitCode).firstResult();

    if (entity == null) {
      throw new WarehouseNotFoundException("Warehouse not found");
    }

    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.archivedAt = warehouse.archivedAt;
    persist(entity);
  }

  @Override
  @Transactional
  public void remove(Warehouse warehouse) {

    delete(
            "businessUnitCode",
            warehouse.businessUnitCode);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {

    DbWarehouse entity =
            find("businessUnitCode", buCode)
                    .firstResult();

    return entity == null ? null : entity.toWarehouse();
  }

  @Override
  public Warehouse findWarehouseById(Long id) {

    DbWarehouse dbWarehouse = find("id", id).firstResult();

    return dbWarehouse != null
            ? dbWarehouse.toWarehouse()
            : null;
  }
}