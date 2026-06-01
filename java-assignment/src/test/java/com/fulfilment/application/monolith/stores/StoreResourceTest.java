package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.fulfilment.application.monolith.stores.helper.LegacyStoreSyncService;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreResourceTest {

    @InjectMock
    LegacyStoreSyncService legacySync;

    private static final String PATH = "/store";

    private Long createStore(String name, int stock) {

        return given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"%s",
                      "quantityProductsInStock":%d
                    }
                    """.formatted(name, stock))
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    void givenStores_whenGetAll_thenReturnList() {

        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200);
    }

    @Test
    void givenExistingStore_whenGetById_thenReturnStore() {

        Long id = createStore("GET_STORE", 10);

        given()
                .when()
                .get(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("GET_STORE"))
                .body("quantityProductsInStock", equalTo(10));
    }

    @Test
    void givenUnknownStore_whenGetById_thenReturn400() {

        given()
                .when()
                .get(PATH + "/999999")
                .then()
                .statusCode(400);
    }

    @Test
    void givenUnknownStore_whenGetById_thenReturnErrorPayload() {

        given()
                .when()
                .get(PATH + "/999999")
                .then()
                .statusCode(400);
    }

    @Test
    void givenValidStore_whenCreate_thenReturn201() {

        reset(legacySync);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"NEW_STORE",
                      "quantityProductsInStock":15
                    }
                    """)
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body("name", equalTo("NEW_STORE"))
                .body("quantityProductsInStock", equalTo(15));

        verify(legacySync).syncCreate(any());
    }

    @Test
    void givenStoreWithId_whenCreate_thenReturn400() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "id":100,
                      "name":"INVALID_STORE",
                      "quantityProductsInStock":10
                    }
                    """)
                .when()
                .post(PATH)
                .then()
                .statusCode(400);
    }

    @Test
    void givenStoreWithoutName_whenCreate_thenReturn400() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "quantityProductsInStock":10
                    }
                    """)
                .when()
                .post(PATH)
                .then()
                .statusCode(400);
    }

    @Test
    void givenExistingStore_whenUpdate_thenReturnUpdatedStore() {

        Long id = createStore("STORE_TO_UPDATE", 10);

        reset(legacySync);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"UPDATED_STORE",
                      "quantityProductsInStock":50
                    }
                    """)
                .when()
                .put(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("UPDATED_STORE"))
                .body("quantityProductsInStock", equalTo(50));

        verify(legacySync).syncUpdate(any());
    }

    @Test
    void givenUnknownStore_whenUpdate_thenReturn400() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"UPDATED_STORE",
                      "quantityProductsInStock":50
                    }
                    """)
                .when()
                .put(PATH + "/999999")
                .then()
                .statusCode(400);
    }

    @Test
    void givenStoreWithoutName_whenUpdate_thenReturn400() {

        Long id = createStore("STORE", 10);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "quantityProductsInStock":50
                    }
                    """)
                .when()
                .put(PATH + "/" + id)
                .then()
                .statusCode(400);
    }

    @Test
    void givenExistingStore_whenPatchName_thenUpdateOnlyName() {

        Long id = createStore("PATCH_STORE", 10);

        reset(legacySync);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"PATCHED_STORE"
                    }
                    """)
                .when()
                .patch(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("PATCHED_STORE"));

        verify(legacySync).syncUpdate(any());
    }

    @Test
    void givenExistingStore_whenPatchStock_thenUpdateOnlyStock() {

        Long id = createStore("PATCH_STOCK_STORE", 10);

        reset(legacySync);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "quantityProductsInStock":99
                    }
                    """)
                .when()
                .patch(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("quantityProductsInStock", equalTo(99));

        verify(legacySync).syncUpdate(any());
    }

    @Test
    void givenUnknownStore_whenPatch_thenReturn400() {

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "name":"PATCHED_STORE"
                    }
                    """)
                .when()
                .patch(PATH + "/999999")
                .then()
                .statusCode(400);
    }

    @Test
    void givenExistingStore_whenDelete_thenReturn204() {

        Long id = createStore("STORE_TO_DELETE", 20);

        given()
                .when()
                .delete(PATH + "/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get(PATH + "/" + id)
                .then()
                .statusCode(400);
    }

    @Test
    void givenUnknownStore_whenDelete_thenReturn400() {

        given()
                .when()
                .delete(PATH + "/999999")
                .then()
                .statusCode(400);
    }
}