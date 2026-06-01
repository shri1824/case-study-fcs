package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;


import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehouseEndpointTest {

  private static final String PATH = "/warehouse";

  @Test
  void givenExistingWarehouses_whenListAll_thenReturnAllActiveWarehouses() {

    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body(
                    containsString("MWH.001"),
                    containsString("MWH.012"),
                    containsString("MWH.023"));
  }

  @Test
  void givenExistingWarehouse_whenGetByBusinessUnitCode_thenReturnWarehouse() {

    given()
            .when()
            .get(PATH + "/MWH.001")
            .then()
            .statusCode(200)
            .body(
                    containsString("MWH.001"),
                    containsString("ZWOLLE-001"));
  }

  @Test
  void givenUnknownBusinessUnitCode_whenGetWarehouse_thenReturn400() {

    given()
            .when()
            .get(PATH + "/UNKNOWN")
            .then()
            .statusCode(400);
  }

  @Test
  void givenValidWarehouseRequest_whenCreateWarehouse_thenReturnCreatedWarehouse() {

    String request =
            """
            {
              "businessUnitCode":"MWH.999",
              "location":"EINDHOVEN-001",
              "capacity":50,
              "stock":25
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(200)
            .body("businessUnitCode", equalTo("MWH.999"))
            .body("location", equalTo("EINDHOVEN-001"))
            .body("capacity", equalTo(50))
            .body("stock", equalTo(25));
  }

  @Test
  void givenExistingBusinessUnitCode_whenCreateWarehouse_thenReturn400() {

    String request =
            """
            {
              "businessUnitCode":"MWH.001",
              "location":"EINDHOVEN-001",
              "capacity":50,
              "stock":25
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(400);
  }

  @Test
  void givenInvalidLocation_whenCreateWarehouse_thenReturn400() {

    String request =
            """
            {
              "businessUnitCode":"MWH.998",
              "location":"INVALID-001",
              "capacity":50,
              "stock":25
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(400);
  }

  @Test
  void givenStockGreaterThanCapacity_whenCreateWarehouse_thenReturn400() {

    String request =
            """
            {
              "businessUnitCode":"MWH.997",
              "location":"EINDHOVEN-001",
              "capacity":10,
              "stock":20
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(400);
  }

  @Test
  void givenCapacityExceedingLocationLimit_whenCreateWarehouse_thenReturn400() {

    String request =
            """
            {
              "businessUnitCode":"MWH.996",
              "location":"ZWOLLE-001",
              "capacity":100,
              "stock":20
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(400);
  }

  @Test
  void givenExistingWarehouse_whenArchiveWarehouse_thenReturn204() {

    String request =
            """
            {
              "businessUnitCode":"MWH.995",
              "location":"EINDHOVEN-001",
              "capacity":50,
              "stock":20
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(200);

    given()
            .when()
            .delete(PATH + "/MWH.995")
            .then()
            .statusCode(204);
  }

  @Test
  void givenUnknownBusinessUnitCode_whenArchiveWarehouse_thenReturn400() {

    given()
            .when()
            .delete(PATH + "/UNKNOWN")
            .then()
            .statusCode(400);
  }

  @Test
  void givenValidReplacementWarehouse_whenReplaceWarehouse_thenArchiveOldAndCreateNew() {

    String replacement =
            """
            {
              "businessUnitCode":"MWH.001",
              "location":"AMSTERDAM-002",
              "capacity":20,
              "stock":10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(replacement)
            .when()
            .post(PATH + "/MWH.001/replacement")
            .then()
            .statusCode(200)
            .body("businessUnitCode", equalTo("MWH.001"))
            .body("location", equalTo("AMSTERDAM-002"))
            .body("stock", equalTo(10));
  }

  @Test
  void givenUnknownBusinessUnitCode_whenReplaceWarehouse_thenReturn404() {

    String replacement =
            """
            {
              "businessUnitCode":"UNKNOWN",
              "location":"AMSTERDAM-002",
              "capacity":20,
              "stock":10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(replacement)
            .when()
            .post(PATH + "/UNKNOWN/replacement")
            .then()
            .statusCode(404);
  }

  @Test
  void givenReplacementWarehouseWithDifferentStock_whenReplaceWarehouse_thenReturn400() {

    String replacement =
            """
            {
              "businessUnitCode":"MWH.001",
              "location":"AMSTERDAM-002",
              "capacity":20,
              "stock":999
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(replacement)
            .when()
            .post(PATH + "/MWH.001/replacement")
            .then()
            .statusCode(400);
  }

  @Test
  void givenReplacementWarehouseWithInsufficientCapacity_whenReplaceWarehouse_thenReturn400() {

    String replacement =
            """
            {
              "businessUnitCode":"MWH.001",
              "location":"AMSTERDAM-002",
              "capacity":5,
              "stock":10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(replacement)
            .when()
            .post(PATH + "/MWH.001/replacement")
            .then()
            .statusCode(400);
  }

  @Test
  void givenArchivedWarehouse_whenListAllWarehouses_thenWarehouseIsNotReturned() {

    String request =
            """
            {
              "businessUnitCode":"MWH.994",
              "location":"EINDHOVEN-001",
              "capacity":50,
              "stock":20
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(PATH)
            .then()
            .statusCode(200);

    given()
            .when()
            .delete(PATH + "/MWH.994")
            .then()
            .statusCode(204);

    given()
            .when()
            .get(PATH)
            .then()
            .statusCode(200)
            .body((containsString("MWH.994")));
  }
}