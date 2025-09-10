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

public class UserCreateTests {

    private UserClient userClient;
    private User testUser;
    private String accessToken;
    private Faker faker;

    @BeforeEach
    void setUp() {
        userClient = new UserClient();
        faker = new Faker();

        testUser = createRandomUser();
    }

    @Test
    void createUniqueUser() {
        Response response = createUser(testUser);
        accessToken = response.path("accessToken"); // сохраняем для удаления

        assertEquals(200, response.getStatusCode());
        assertEquals(true, response.path("success"));
    }

    @Test
    void createAlreadyRegisteredUser() {
        Response firstResponse = createUser(testUser);
        String token = firstResponse.path("accessToken");

        // пробуем создать снова
        Response response = createUser(testUser);

        assertEquals(403, response.getStatusCode());
        assertEquals("User already exists", response.path("message"));

        deleteUser(token);
    }

    @Test
    void createUserWithoutRequiredField() {
        User incompleteUser = new User(null, "123456", "Test User"); // нет email
        Response response = createUser(incompleteUser);

        assertEquals(403, response.getStatusCode());
        assertEquals("Email, password and name are required fields", response.path("message"));
    }

    @Step("Создать случайного пользователя")
    private User createRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 12),
                faker.name().fullName()
        );
    }

    @Step("Создать пользователя через API: {user}")
    private Response createUser(User user) {
        return userClient.createUser(user);
    }

    @Step("Удалить пользователя через API")
    private void deleteUser(String token) {
        userClient.deleteUser(token);
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }
}
