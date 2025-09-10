package ru.praktikum.tests.order;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.praktikum.pojo.User;
import ru.praktikum.service.OrderClient;
import ru.praktikum.service.UserClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetUserOrdersTests {

    private UserClient userClient;
    private OrderClient orderClient;
    private Faker faker;
    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        faker = new Faker();

        // Создаём пользователя и получаем токен
        testUser = new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 12),
                faker.name().fullName()
        );
        Response response = createUser(testUser);
        accessToken = response.path("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    void getUserOrdersWithAuth() {
        Response response = getUserOrders(accessToken);

        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    void getUserOrdersWithoutAuth() {
        Response response = getUserOrders(null);

        assertEquals(401, response.getStatusCode());
        assertEquals(false, response.path("success"));
        assertEquals("You should be authorised", response.path("message"));
    }

    @Step("Создать пользователя через API")
    private Response createUser(User user) {
        Response response = userClient.createUser(user);
        assertEquals(200, response.getStatusCode());
        return response;
    }

    @Step("Удалить пользователя по accessToken")
    private Response deleteUser(String accessToken) {
        return userClient.deleteUser(accessToken);
    }

    @Step("Получить заказы пользователя")
    private Response getUserOrders(String token) {
        return orderClient.getUserOrders(token);
    }
}
