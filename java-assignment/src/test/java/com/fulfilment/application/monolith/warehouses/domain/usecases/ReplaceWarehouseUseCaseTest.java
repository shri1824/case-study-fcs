package com.fulfilment.application.monolith.warehouses.domain.usecases;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.WarehouseNotFoundException;
import com.fulfilment.application.monolith.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ReplaceWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ReplaceWarehouseUseCase useCase;
    private LocationResolver locationResolver;

    @BeforeEach
    void setup() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);

        useCase =
                new ReplaceWarehouseUseCase(
                        warehouseStore,
                        locationResolver);
    }

    @Test
    void givenExistingWarehouse_whenReplace_thenArchiveOldAndCreateNew() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.location = "ZWOLLE-001";
        current.stock = 20;
        current.capacity = 40;

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.location = "EINDHOVEN-001";
        replacement.stock = 20;
        replacement.capacity = 50;

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);

        when(locationResolver.resolveByIdentifier("EINDHOVEN-001"))
                .thenReturn(
                        new Location(
                                "EINDHOVEN-001",
                                2,
                                70));

        useCase.replace(replacement);

        assertNotNull(current.archivedAt);

        verify(warehouseStore).update(current);
        verify(warehouseStore).create(replacement);
    }

    @Test
    void givenNonExistingWarehouse_whenReplace_thenThrowNotFoundException() {

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "UNKNOWN";

        when(warehouseStore.findByBusinessUnitCode("UNKNOWN"))
                .thenReturn(null);

        assertThrows(
                WarehouseNotFoundException.class,
                () -> useCase.replace(replacement));

        verify(warehouseStore, never()).update(any());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void givenDifferentStock_whenReplace_thenThrowValidationException() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.stock = 20;

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.stock = 10;
        replacement.capacity = 30;

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement));

        verify(warehouseStore, never()).update(any());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void givenCapacityLessThanCurrentStock_whenReplace_thenThrowValidationException() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.stock = 20;

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.stock = 20;
        replacement.capacity = 15;

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement));

        verify(warehouseStore, never()).update(any());
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void givenCapacityExceedsLocationLimit_whenReplace_thenThrowValidationException() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.stock = 10;


        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.location = "AMSTERDAM-001";
        replacement.capacity = 200;
        replacement.stock = 10;


        Location location =
                new Location("AMSTERDAM-001", 5, 100);


        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);


        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(location);


        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement));

        verify(warehouseStore, never())
                .create(any());

        verify(warehouseStore, never())
                .update(any());
    }

    @Test
    void givenStockGreaterThanCapacity_whenReplace_thenThrowValidationException() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.stock = 60;

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.capacity = 50;
        replacement.stock = 60;

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement));
    }
    @Test
    void givenNullStock_whenReplace_thenThrowValidationException() {

        Warehouse current = new Warehouse();
        current.businessUnitCode = "MWH.001";
        current.stock = 10;

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "MWH.001";
        replacement.stock = null;

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(current);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement));
    }
}