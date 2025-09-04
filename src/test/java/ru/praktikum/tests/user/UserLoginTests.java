package ru.praktikum.tests.user;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.praktikum.pojo.User;
import ru.praktikum.service.UserClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserLoginTests {

    private UserClient userClient;
    private Faker faker;
    private String accessToken; // Для удаления пользователя после теста

    @BeforeEach
    void setUp() {
        userClient = new UserClient();
        faker = new Faker();
    }

    @ParameterizedTest(name = "Сценарий логина: {0}")
    @ValueSource(strings = {"correct", "wrongPassword", "wrongEmail"})
    void loginTest(String scenario) {
        User user = createRandomUser();

        switch (scenario) {
            case "correct":
                createUser(user);
                break;
            case "wrongPassword":
                createUser(user);
                user.setPassword("wrongPassword");
                break;
            case "wrongEmail":
                createUser(user);
                user.setEmail("wrong_" + user.getEmail());
                break;
        }

        Response response = loginUser(user);

        verifyLoginResponse(response, scenario);
    }

    @Step("Создание случайного пользователя")
    private User createRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 12),
                faker.name().fullName()
        );
    }

    @Step("Создание пользователя через API")
    private void createUser(User user) {
        Response response = userClient.createUser(user);
        assertEquals(200, response.getStatusCode(), "Ошибка при создании пользователя");
        accessToken = response.path("accessToken"); // сохраняем для удаления
    }

    @Step("Логин пользователя через API")
    private Response loginUser(User user) {
        return userClient.loginUser(user);
    }

    @Step("Проверка ответа логина для сценария: {scenario}")
    private void verifyLoginResponse(Response response, String scenario) {
        switch (scenario) {
            case "correct":
                assertEquals(200, response.getStatusCode(), "Успешный логин должен вернуть 200");
                assertEquals(true, response.path("success"), "Поле success должно быть true");
                break;
            case "wrongPassword":
            case "wrongEmail":
                assertEquals(401, response.getStatusCode(), "Неверные данные должны вернуть 401");
                assertEquals(false, response.path("success"), "Поле success должно быть false");
                assertEquals("email or password are incorrect", response.path("message"), "Сообщение об ошибке неверное");
                break;
        }
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
            accessToken = null;
        }
    }
}
