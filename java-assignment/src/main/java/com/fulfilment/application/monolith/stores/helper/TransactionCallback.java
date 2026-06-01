package com.fulfilment.application.monolith.stores.helper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.jboss.logging.Logger;


@ApplicationScoped
public class TransactionCallback {

    private static final Logger LOGGER =
            Logger.getLogger(TransactionCallback.class);

    @Inject
    TransactionSynchronizationRegistry txRegistry;

    public void afterCommit(Runnable action) {

        txRegistry.registerInterposedSynchronization(
                new Synchronization() {

                    @Override
                    public void beforeCompletion() {
                    }

                    @Override
                    public void afterCompletion(int status) {

                        if (status != Status.STATUS_COMMITTED) {
                            return;
                        }

                        try {
                            action.run();
                        } catch (Exception ex) {
                            LOGGER.error(
                                    "Post-commit action failed",
                                    ex);
                        }
                    }
                });
    }
}