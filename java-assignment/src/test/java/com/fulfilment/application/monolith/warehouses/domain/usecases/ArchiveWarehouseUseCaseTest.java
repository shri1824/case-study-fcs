package com.fulfilment.application.monolith.warehouses.domain.usecases;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArchiveWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        useCase = new ArchiveWarehouseUseCase();
        useCase.warehouseStore = warehouseStore;
    }

    @Test
    void givenActiveWarehouse_whenArchive_thenWarehouseArchivedAndUpdated() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);

        verify(warehouseStore, times(1)).update(warehouse);
    }

    @Test
    void givenAlreadyArchivedWarehouse_whenArchive_thenThrowValidationException() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.001";
        warehouse.archivedAt = LocalDateTime.now();

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.archive(warehouse));

        verify(warehouseStore, never()).update(any());
    }
    @Test
    void givenNullWarehouse_whenArchive_thenThrowValidationException() {

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.archive(null));

        verify(warehouseStore, never())
                .update(any());
    }
}