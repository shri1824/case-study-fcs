package com.fulfilment.application.monolith.warehouses.adapters.database;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository repository;

    @Inject
    EntityManager entityManager;

    @AfterEach
    @Transactional
    void cleanDatabase() {

        entityManager
                .createQuery("""
                delete from DbWarehouse
                where businessUnitCode like 'TEST.%'
            """)
                .executeUpdate();
    }

    @Test
    @Transactional
    void givenWarehouse_whenCreate_thenCanFindByBusinessUnitCode() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "TEST.001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 50;
        warehouse.stock = 10;


        repository.create(warehouse);


        Warehouse result =
                repository.findByBusinessUnitCode("TEST.001");


        assertNotNull(result);
        assertEquals("TEST.001", result.businessUnitCode);
        assertEquals("ZWOLLE-001", result.location);
        assertEquals(50, result.capacity);
        assertEquals(10, result.stock);
        assertNotNull(result.id);
    }


    @Test
    @Transactional
    void givenWarehouse_whenFindById_thenReturnWarehouse() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "TEST.002";
        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 100;
        warehouse.stock = 20;


        repository.create(warehouse);


        Warehouse result =
                repository.findWarehouseById(warehouse.id);


        assertNotNull(result);
        assertEquals(warehouse.id, result.id);
        assertEquals("TEST.002", result.businessUnitCode);
        assertEquals("AMSTERDAM-001", result.location);
    }


    @Test
    void givenUnknownBusinessUnitCode_whenFind_thenReturnNull() {

        Warehouse result =
                repository.findByBusinessUnitCode("UNKNOWN");


        assertNull(result);
    }


    @Test
    void givenUnknownId_whenFindById_thenReturnNull() {

        Warehouse result =
                repository.findWarehouseById(999999L);


        assertNull(result);
    }


    @Test
    @Transactional
    void givenWarehouse_whenUpdate_thenPersistChanges() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "TEST.003";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 50;
        warehouse.stock = 10;


        repository.create(warehouse);


        warehouse.capacity = 80;
        warehouse.stock = 25;
        warehouse.location = "AMSTERDAM-001";


        repository.update(warehouse);


        Warehouse updated =
                repository.findByBusinessUnitCode("TEST.003");


        assertNotNull(updated);
        assertEquals(80, updated.capacity);
        assertEquals(25, updated.stock);
        assertEquals(
                "AMSTERDAM-001",
                updated.location);
    }


    @Test
    @Transactional
    void givenWarehouse_whenRemove_thenWarehouseIsDeleted() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "TEST.004";
        warehouse.location = "TILBURG-001";
        warehouse.capacity = 40;
        warehouse.stock = 5;


        repository.create(warehouse);


        repository.remove(warehouse);


        Warehouse result =
                repository.findByBusinessUnitCode("TEST.004");


        assertNull(result);
    }


    @Test
    @Transactional
    void givenWarehouse_whenCreate_thenDomainIdIsPopulated() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "TEST.005";
        warehouse.location = "EINDHOVEN-001";
        warehouse.capacity = 60;
        warehouse.stock = 20;


        repository.create(warehouse);


        assertNotNull(warehouse.id);
    }

}