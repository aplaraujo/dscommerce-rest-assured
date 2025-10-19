package com.example.dscommerce_rest_assured.controllers;
import com.example.dscommerce_rest_assured.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {
    private String clientUsername, clientPasssword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingOrderId, notExistingOrderId;

    @BeforeEach
    public void setUp() {
        baseURI = "http://localhost:8080";

        existingOrderId = 1L;
        notExistingOrderId = 100L;

        clientUsername = "maria@gmail.com";
        clientPasssword = "123456";
        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPasssword);

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);

        invalidToken = adminToken + "xpto";
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminIsLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("status", equalTo("PAID"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientIsLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("status", equalTo("PAID"))
                .body("client.name", equalTo("Maria Brown"))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0F));
    }

    @Test
    public void findByIdShouldReturnForbiddenWhenIdExistsAndIsOrderOfAnotherClient() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/orders/{id}", 2)
                .then()
                .statusCode(403);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenDoesNotExistAndAdminIsLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/orders/{id}", notExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenDoesNotExistAndClientIsLogged() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .when()
                .get("/orders/{id}", notExistingOrderId)
                .then()
                .statusCode(404);
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenTokenIsInvalid() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);
    }
}
