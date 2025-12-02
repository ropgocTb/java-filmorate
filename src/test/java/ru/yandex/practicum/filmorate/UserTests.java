package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTests {
    UserController controller;

    @BeforeEach
    public void init() {
        UserStorage storage = new InMemoryUserStorage();
        controller = new UserController(new UserService(storage));
    }

    @Test
    public void userValidInputAddTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("123@");
        user.setLogin("asdfasdfasdf");
        user.setBirthday(LocalDate.of(2002, Month.DECEMBER, 2));

        controller.addUser(user);
        assertEquals(1, controller.getUsers().size(), "пользователь не добавился");
    }

    @Test
    public void userEmptyEmailTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("");
        user.setLogin("asdfasdfasdf");
        user.setBirthday(LocalDate.of(2002, Month.DECEMBER, 2));

        assertThrows(RuntimeException.class, () -> {
            controller.addUser(user);
        });
    }

    @Test
    public void userEmailDoesNotContainAtSymbolTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("123");
        user.setLogin("asdfasdfasdf");
        user.setBirthday(LocalDate.of(2002, Month.DECEMBER, 2));

        assertThrows(RuntimeException.class, () -> {
            controller.addUser(user);
        });
    }

    @Test
    public void userLoginEmptyTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("123@");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2002, Month.DECEMBER, 2));

        assertThrows(RuntimeException.class, () -> {
            controller.addUser(user);
        });
        assertEquals(0, controller.getUsers().size(), "пользователь с пустым логином добавился");
    }

    @Test
    public void userEmptyNameReplacedWithLoginTest() {
        User user = new User();
        user.setEmail("123@");
        user.setLogin("asdfasdfasdf");
        user.setBirthday(LocalDate.of(2002, Month.DECEMBER, 2));

        controller.addUser(user);
        assertEquals(user.getLogin(), controller.getUsers().getFirst().getName(), "пустое имя не " +
                "заменилось на логин");
    }

    @Test
    public void userFutureBirthdateTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("123@");
        user.setLogin("asdfasdfasdf");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(RuntimeException.class, () -> {
            controller.addUser(user);
        });
        assertEquals(0, controller.getUsers().size(), "пользователь который родится" +
                " завтра добавился");
    }

    @Test
    public void userUpdateTest() {
        User user = new User();
        user.setName("Andrew");
        user.setEmail("123@");
        user.setLogin("123");
        user.setBirthday(LocalDate.now().minusDays(12));

        controller.addUser(user);

        user.setLogin("456");

        controller.updateUser(user);

        assertEquals("456", controller.getUsers().getFirst().getLogin(), "пользователь не обновился");
    }

    //тест пустого запроса
    @Test
    public void emptyRequestTest() {
        assertThrows(NullPointerException.class, () -> {
            controller.addUser(null);
        });
    }

    @Test
    public void loginWithAWhiteSpaceTest() {
        User user = new User();
        user.setLogin("1 1");
        user.setBirthday(LocalDate.now().minusDays(1));
        user.setEmail("123@");
        user.setName("Name");

        assertThrows(RuntimeException.class, () -> {
           controller.addUser(user);
        });
        assertEquals(0, controller.getUsers().size(), "пользователь с пробелом в логине добавился");
    }
}
