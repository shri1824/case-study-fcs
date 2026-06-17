package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StoreTestDataFactory {

    @Transactional
    public Store createStoreForDeleteTest() {

        Store store = new Store(
                "DELETE_TEST_" + System.currentTimeMillis()
        );

        store.quantityProductsInStock = 10;

        store.persistAndFlush();

        return store;
    }
}
