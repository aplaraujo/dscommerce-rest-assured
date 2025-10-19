package com.example.dscommerce_rest_assured.tests;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.*;

public class TokenUtil {

    public static String obtainAccessToken(String username, String password) {
        Response response = authRequest(username, password);
        response.then().log().all(); // mostra status e corpo
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to obtain access token for user: " + username);
        }
        return response.jsonPath().getString("access_token");
    }

    private static Response authRequest(String username, String password) {
        return given()
                .auth()
                .preemptive()
                .basic("myclientid", "myclientsecret")
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/oauth2/token");
    }
}
