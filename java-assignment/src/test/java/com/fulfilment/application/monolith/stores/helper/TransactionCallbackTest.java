package com.fulfilment.application.monolith.stores.helper;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.atomic.AtomicBoolean;

@QuarkusTest
class TransactionCallbackTest {

    @Inject
    TransactionCallback transactionCallback;

    @InjectMock
    TransactionSynchronizationRegistry txRegistry;

    @Test
    void shouldExecuteActionAfterCommit() {

        AtomicBoolean executed = new AtomicBoolean(false);

        transactionCallback.afterCommit(
                () -> executed.set(true));

        ArgumentCaptor<Synchronization> captor =
                ArgumentCaptor.forClass(Synchronization.class);

        verify(txRegistry)
                .registerInterposedSynchronization(
                        captor.capture());

        Synchronization synchronization =
                captor.getValue();

        synchronization.afterCompletion(
                Status.STATUS_COMMITTED);

        assertTrue(executed.get());
    }

    @Test
    void shouldNotExecuteActionWhenTransactionRolledBack() {

        AtomicBoolean executed = new AtomicBoolean(false);

        transactionCallback.afterCommit(
                () -> executed.set(true));

        ArgumentCaptor<Synchronization> captor =
                ArgumentCaptor.forClass(Synchronization.class);

        verify(txRegistry)
                .registerInterposedSynchronization(
                        captor.capture());

        Synchronization synchronization =
                captor.getValue();

        synchronization.afterCompletion(
                Status.STATUS_ROLLEDBACK);

        assertFalse(executed.get());
    }

    @Test
    void shouldNotThrowWhenActionFails() {

        transactionCallback.afterCommit(
                () -> {
                    throw new RuntimeException(
                            "Simulated failure");
                });

        ArgumentCaptor<Synchronization> captor =
                ArgumentCaptor.forClass(Synchronization.class);

        verify(txRegistry)
                .registerInterposedSynchronization(
                        captor.capture());

        Synchronization synchronization =
                captor.getValue();

        assertDoesNotThrow(() ->
                synchronization.afterCompletion(
                        Status.STATUS_COMMITTED));
    }

    @Test
    void beforeCompletionShouldBeCallable() {

        transactionCallback.afterCommit(() -> {});

        ArgumentCaptor<Synchronization> captor =
                ArgumentCaptor.forClass(Synchronization.class);

        verify(txRegistry)
                .registerInterposedSynchronization(
                        captor.capture());

        Synchronization synchronization =
                captor.getValue();

        assertDoesNotThrow(
                synchronization::beforeCompletion);
    }
}