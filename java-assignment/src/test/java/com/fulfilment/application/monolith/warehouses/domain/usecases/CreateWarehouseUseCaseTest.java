package com.fulfilment.application.monolith.warehouses.domain.usecases;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.WarehouseValidationException;


import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setup() {

        warehouseStore = Mockito.mock(WarehouseStore.class);
        locationResolver = Mockito.mock(LocationResolver.class);

        useCase = new CreateWarehouseUseCase();
        useCase.warehouseStore = warehouseStore;
        useCase.locationResolver = locationResolver;
    }

    @Test
    void givenValidWarehouse_whenCreate_thenPersistWarehouse() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";
        warehouse.location = "EINDHOVEN-001";
        warehouse.capacity = 50;
        warehouse.stock = 20;

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("EINDHOVEN-001"))
                .thenReturn(new Location("EINDHOVEN-001", 2, 70));

        useCase.create(warehouse);

        verify(warehouseStore).create(warehouse);
    }

    @Test
    void givenExistingBusinessUnitCode_whenCreate_thenThrowValidationException() {

        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "MWH.100";

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(existing);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(warehouse));

        verify(warehouseStore, never()).create(any());
    }

    @Test
    void givenCapacityGreaterThanLocationLimit_whenCreate_thenThrowValidationException() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.101";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 100;
        warehouse.stock = 10;

        when(warehouseStore.findByBusinessUnitCode("MWH.101"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("ZWOLLE-001"))
                .thenReturn(new Location("ZWOLLE-001", 1, 40));

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(warehouse));

        verify(warehouseStore, never()).create(any());
    }

    @Test
    void givenStockGreaterThanCapacity_whenCreate_thenThrowValidationException() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.102";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 20;
        warehouse.stock = 30;

        when(warehouseStore.findByBusinessUnitCode("MWH.102"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("ZWOLLE-001"))
                .thenReturn(new Location("ZWOLLE-001", 1, 40));

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(warehouse));

        verify(warehouseStore, never()).create(any());
    }
}