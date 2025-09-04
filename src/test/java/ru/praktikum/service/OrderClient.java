package ru.praktikum.service;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.praktikum.pojo.Order;

import java.util.List;

public class OrderClient extends Client {

    @Step("Создать заказ")
    public Response createOrder(Order order, String accessToken) {
        io.restassured.specification.RequestSpecification request = spec()
                .body(order);
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.when().post("/orders");
    }

    @Step("Получить список заказов пользователя")
    public Response getUserOrders(String accessToken) {
        io.restassured.specification.RequestSpecification request = spec();
        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request.when().get("/orders");
    }

    @Step("Получить все заказы (без авторизации)")
    public Response getAllOrders() {
        return spec().when().get("/orders/all");
    }

    @Step("Получить все ингредиенты")
    public List<String> getAllIngredients() {
        Response response = spec()
                .when()
                .get("/ingredients");
        // извлекаем из ответа список id ингредиентов
        return response.jsonPath().getList("data._id", String.class);
    }
}
