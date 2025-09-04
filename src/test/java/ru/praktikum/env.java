package ru.praktikum;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class env {
    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
    }
}
