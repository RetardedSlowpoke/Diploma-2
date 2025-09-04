package ru.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Client {

    @Step("Создать пользователя {email}")
    public Response createUser(String email, String password, String name) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("name", name);

        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/auth/register");
    }

    @Step("Удалить пользователя по accessToken")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }

    @Step("Логин пользователя {email}")
    public Response loginUser(String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        return given()
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/auth/login");
    }
}
