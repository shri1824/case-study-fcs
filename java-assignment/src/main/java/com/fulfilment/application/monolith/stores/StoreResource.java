package com.fulfilment.application.monolith.stores;

import java.net.URI;
import java.util.List;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.stores.helper.LegacyStoreSyncService;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Path("/store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  private static final Logger LOGGER = Logger.getLogger(StoreResource.class);

  private static final String STORE_NOT_FOUND = "Store with id %d does not exist.";
  private static final String STORE_NAME_REQUIRED = "Store name is required.";
  private static final String STORE_ID_NOT_ALLOWED = "Id must not be provided when creating a store.";

  @Inject
  LegacyStoreSyncService legacySync;

  @GET
  public List<Store> get() {
    LOGGER.debug("Fetching all stores");
    return Store.listAll(Sort.by("name"));
  }

  @GET
  @Path("/{id}")
  public Store getSingle(@PathParam("id") Long id) {
    LOGGER.debugf("Fetching store with id=%d", id);
    return findStoreOrThrow(id);
  }

  @POST
  @Transactional
  public Response create(Store store) {
    LOGGER.info("Creating new store");

    if (store.id != null) {
      throw new WebApplicationException(
              STORE_ID_NOT_ALLOWED,
              Status.BAD_REQUEST);
    }

    validateStore(store);

    store.persist();

    LOGGER.infof("Store created successfully. id=%d", store.id);

    legacySync.syncCreate(store);

    return Response.created(URI.create("/store/" + store.id))
            .entity(store)
            .build();
  }

  @PUT
  @Path("/{id}")
  @Transactional
  public Store update(@PathParam("id") Long id, Store updatedStore) {

    LOGGER.infof("Updating store id=%d", id);

    validateStore(updatedStore);

    Store entity = findStoreOrThrow(id);

    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

    // Using sync to avoid data inconsistency.
    legacySync.syncUpdate(entity);

    LOGGER.infof("Store updated successfully. id=%d", id);

    return entity;
  }

  @PATCH
  @Path("/{id}")
  @Transactional
  public Store patch(@PathParam("id") Long id, Store updatedStore) {

    LOGGER.infof("Partially updating store id=%d", id);

    Store entity = findStoreOrThrow(id);

    if (updatedStore.name != null && !updatedStore.name.isBlank()) {
      entity.name = updatedStore.name;
    }

    if (updatedStore.quantityProductsInStock != null) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }

    legacySync.syncUpdate(entity);

    LOGGER.infof("Store patched successfully. id=%d", id);

    return entity;
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  public Response delete(@PathParam("id") Long id) {

    LOGGER.infof("Deleting store id=%d", id);

    Store entity = findStoreOrThrow(id);

    legacySync.syncDelete(entity);

    entity.delete();

    LOGGER.infof("Store deleted successfully. id=%d", id);

    return Response.noContent().build();
  }

  private Store findStoreOrThrow(Long id) {
    Store store = Store.findById(id);

    if (store == null) {
      throw new WebApplicationException(
              String.format(STORE_NOT_FOUND, id),
              Status.NOT_FOUND);
    }

    return store;
  }

  private void validateStore(Store store) {
    if (store == null || store.name == null || store.name.isBlank()) {
      throw new WebApplicationException(
              STORE_NAME_REQUIRED,
              422);
    }
  }
}