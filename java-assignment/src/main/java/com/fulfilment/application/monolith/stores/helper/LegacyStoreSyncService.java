package com.fulfilment.application.monolith.stores.helper;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;


@ApplicationScoped
public class LegacyStoreSyncService {

    private static final Logger LOGGER =
            Logger.getLogger(LegacyStoreSyncService.class);

    @Inject
    LegacyStoreManagerGateway gateway;

    @Inject
    TransactionCallback txCallback;

    public void syncCreate(Store store) {

        txCallback.afterCommit(() -> {
            try {
                gateway.createStoreOnLegacySystem(store);

                LOGGER.infof(
                        "Legacy create sync successful. storeId=%d",
                        store.id);

            } catch (Exception ex) {

                LOGGER.errorf(
                        ex,
                        "Legacy create sync failed. storeId=%d",
                        store.id);
            }
        });
    }

    public void syncUpdate(Store store) {

        txCallback.afterCommit(() -> {
            try {
                gateway.updateStoreOnLegacySystem(store);

                LOGGER.infof(
                        "Legacy update sync successful. storeId=%d",
                        store.id);

            } catch (Exception ex) {

                LOGGER.errorf(
                        ex,
                        "Legacy update sync failed. storeId=%d",
                        store.id);
            }
        });
    }
}
