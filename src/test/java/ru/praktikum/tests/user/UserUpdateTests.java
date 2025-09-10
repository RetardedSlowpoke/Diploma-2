package ru.praktikum.tests.user;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.praktikum.pojo.User;
import ru.praktikum.service.UserClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserUpdateTests {

    private UserClient userClient;
    private Faker faker;
    private User testUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userClient = new UserClient();
        faker = new Faker();

        testUser = createRandomUser();

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
    void updateUserWithAuth() {
        User updatedUser = createRandomUser();
        updatedUser.setName("Updated Name");

        Response response = updateUser(updatedUser, accessToken);

        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    void updateUserWithoutAuth() {
        User updatedUser = createRandomUser();
        updatedUser.setName("Updated Name");

        Response response = updateUser(updatedUser, null);

        assertEquals(401, response.getStatusCode());
        assertEquals(false, response.path("success"));
        assertEquals("You should be authorised", response.path("message"));
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
    private Response createUser(User user) {
        Response response = userClient.createUser(user);
        assertEquals(200, response.getStatusCode());
        return response;
    }

    @Step("Обновление пользователя через API")
    private Response updateUser(User user, String token) {

        return userClient.updateUser(token, user);
    }

    @Step("Удалить пользователя по accessToken")
    private Response deleteUser(String accessToken) {
        return userClient.deleteUser(accessToken);
    }
}
