package ru.praktikum.tests.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.praktikum.pojo.Order;
import ru.praktikum.service.OrderClient;
import ru.praktikum.service.UserClient;
import ru.praktikum.pojo.User;
import com.github.javafaker.Faker;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateOrderTests {

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



    @Test
    void createOrderWithAuth() {
        List<String> randomIngredients = getRandomIngredients(2);
        Order order = new Order(randomIngredients);
        Response response = createOrder(order, accessToken);
        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    void createOrderWithoutAuth() {
        List<String> randomIngredients = getRandomIngredients(2);
        Order order = new Order(randomIngredients);
        Response response = createOrder(order, null);
        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    void createOrderWithEmptyIngredients() {
        Order order = new Order(List.of());
        Response response = createOrder(order, accessToken);
        assertEquals(400, response.getStatusCode());
        assertEquals(false, response.path("success"));
        assertEquals("Ingredient ids must be provided", response.path("message"));
    }

    @Test
    void createOrderWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalidIngredientHash"));
        Response response = createOrder(order, accessToken);
        assertEquals(500, response.getStatusCode());
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

    @Step("Создать заказ")
    private Response createOrder(Order order, String token) {
        return orderClient.createOrder(order, token);
    }

    @Step("Получить два случайных ингредиента")
    private List<String> getRandomIngredients(int count) {
        List<String> allIngredients = orderClient.getAllIngredients(); // метод получаем из OrderClient
        Random random = new Random();
        return random.ints(0, allIngredients.size())
                .distinct()
                .limit(count)
                .mapToObj(allIngredients::get)
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }
}
