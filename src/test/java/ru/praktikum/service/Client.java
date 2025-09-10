package ru.praktikum.service;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class Client {
    public static final String BASE_PATH = "/api";

    public RequestSpecification spec() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(BaseURL.BASE_URL)
                .basePath(BASE_PATH);
    }
}
