package com.fulfilment.application.monolith.products;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductEndpointTest {

  private static final String PATH = "/product";

  @Test
  void givenExistingProducts_whenGetAll_thenReturnProducts() {

    // When / Then
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(
                    containsString("TONSTAD"),
                    containsString("KALLAX"),
                    containsString("BESTÅ")
            );
  }

  @Test
  void givenExistingProduct_whenDelete_thenProductIsRemoved() {

    // When
    given()
            .when()
            .delete(PATH + "/1")
            .then()
            .statusCode(204);

    // Then
    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(
                    not(containsString("TONSTAD")),
                    containsString("KALLAX"),
                    containsString("BESTÅ")
            );
  }

  @Test
  void givenNonExistingProduct_whenGetSingle_thenReturn404() {

    // When / Then
    given()
            .when()
            .get(PATH + "/99999")
            .then()
            .statusCode(404);
  }

  @Test
  void givenNonExistingProduct_whenDelete_thenReturn404() {

    // When / Then
    given()
            .when()
            .delete(PATH + "/99999")
            .then()
            .statusCode(404);
  }

  @Test
  void givenValidProduct_whenCreate_thenReturn201() {

    String requestBody = """
            {
                "name":"OFFICE_DESK",
                "description":"Standing desk",
                "price":199.99,
                "stock":10
            }
            """;

    // When / Then
    given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(PATH)
            .then()
            .statusCode(201)
            .body("name", equalTo("OFFICE_DESK"))
            .body("description", equalTo("Standing desk"))
            .body("stock", equalTo(10));
  }

  @Test
  void givenProductWithId_whenCreate_thenReturn422() {

    String requestBody = """
            {
                "id":100,
                "name":"INVALID_PRODUCT",
                "description":"Should fail",
                "price":50.00,
                "stock":1
            }
            """;

    // When / Then
    given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(PATH)
            .then()
            .statusCode(422);
  }

  @Test
  void givenExistingProduct_whenUpdate_thenReturnUpdatedProduct() {

    String requestBody = """
            {
                "name":"UPDATED_KALLAX",
                "description":"Updated description",
                "price":149.99,
                "stock":25
            }
            """;

    // When / Then
    given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .put(PATH + "/2")
            .then()
            .statusCode(200)
            .body("name", equalTo("UPDATED_KALLAX"))
            .body("description", equalTo("Updated description"))
            .body("stock", equalTo(25));
  }

  @Test
  void givenUpdateWithoutName_whenUpdate_thenReturn422() {

    String requestBody = """
            {
                "description":"Updated description",
                "price":149.99,
                "stock":25
            }
            """;

    // When / Then
    given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .put(PATH + "/2")
            .then()
            .statusCode(422);
  }

  @Test
  void givenNonExistingProduct_whenUpdate_thenReturn404() {

    String requestBody = """
            {
                "name":"UPDATED_PRODUCT",
                "description":"Updated description",
                "price":149.99,
                "stock":25
            }
            """;

    // When / Then
    given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .put(PATH + "/99999")
            .then()
            .statusCode(404);
  }
}
