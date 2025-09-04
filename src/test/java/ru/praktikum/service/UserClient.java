package ru.praktikum.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikum.pojo.User;


public class UserClient extends Client {

    @Step("Создать пользователя {user.email}")
    public Response createUser(User user) {
        return spec()
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Удалить пользователя по accessToken")
    public Response deleteUser(String accessToken) {
        return spec()
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }

    @Step("Логин пользователя {user.email}")
    public Response loginUser(User user) {
        return spec()
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Изменить данные пользователя")
    public Response updateUser(String accessToken, User newData) {
        io.restassured.specification.RequestSpecification request = spec().body(newData);
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.when().patch("/auth/user");
    }
}
