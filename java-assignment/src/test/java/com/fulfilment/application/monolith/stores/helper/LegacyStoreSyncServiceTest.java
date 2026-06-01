package com.fulfilment.application.monolith.stores.helper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LegacyStoreSyncServiceTest {

    @Inject
    LegacyStoreSyncService service;

    @InjectMock
    LegacyStoreManagerGateway gateway;

    @InjectMock
    TransactionCallback txCallback;

    @Test
    void syncCreate_shouldInvokeGatewayAfterCommit() {

        Store store = new Store();
        store.id = 1L;
        store.name = "STORE_1";

        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run();
            return null;
        }).when(txCallback).afterCommit(any(Runnable.class));

        service.syncCreate(store);

        verify(txCallback, times(1))
                .afterCommit(any(Runnable.class));

        verify(gateway, times(1))
                .createStoreOnLegacySystem(store);
    }

    @Test
    void syncUpdate_shouldInvokeGatewayAfterCommit() {

        Store store = new Store();
        store.id = 1L;
        store.name = "STORE_1";

        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run();
            return null;
        }).when(txCallback).afterCommit(any(Runnable.class));

        service.syncUpdate(store);

        verify(txCallback, times(1))
                .afterCommit(any(Runnable.class));

        verify(gateway, times(1))
                .updateStoreOnLegacySystem(store);
    }

    @Test
    void syncCreate_shouldNotThrowWhenGatewayFails() {

        Store store = new Store();
        store.id = 1L;

        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run();
            return null;
        }).when(txCallback).afterCommit(any(Runnable.class));

        doThrow(new RuntimeException("Legacy failure"))
                .when(gateway)
                .createStoreOnLegacySystem(store);

        service.syncCreate(store);

        verify(gateway, times(1))
                .createStoreOnLegacySystem(store);
    }

    @Test
    void syncUpdate_shouldNotThrowWhenGatewayFails() {

        Store store = new Store();
        store.id = 1L;

        doAnswer(invocation -> {
            Runnable callback = invocation.getArgument(0);
            callback.run();
            return null;
        }).when(txCallback).afterCommit(any(Runnable.class));

        doThrow(new RuntimeException("Legacy failure"))
                .when(gateway)
                .updateStoreOnLegacySystem(store);

        service.syncUpdate(store);

        verify(gateway, times(1))
                .updateStoreOnLegacySystem(store);
    }
}