package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UsersDbTest {
    private final UserDbStorage userStorage;
    private final UserService service;
    private final JdbcTemplate template;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void init() {

        user1 = User.builder()
                .id(1)
                .name("Name")
                .login("Login")
                .email("123@")
                .birthday(LocalDate.now().minusDays(1))
                .userFriends(new ArrayList<>())
                .build();
        user2 = User.builder()
                .id(2)
                .name("Namdddde")
                .login("Login")
                .email("123@")
                .birthday(LocalDate.now().minusDays(1))
                .userFriends(new ArrayList<>())
                .build();
        user3 = User.builder()
                .id(3)
                .name("Nameasdf")
                .login("Login")
                .email("123@")
                .birthday(LocalDate.now().minusDays(1))
                .userFriends(new ArrayList<>())
                .build();
        userStorage.addUser(user1);
        userStorage.addUser(user2);
    }

    @Test
    public void addUserTest() {
        userStorage.addUser(user3);
        assertEquals(userStorage.getUserById(user3.getId()), user3, "пользователь не добавился");
    }

    @Test
    public void updateUserTest() {
        user2.setName("New name");
        userStorage.updateUser(user2);
        assertEquals("New name", userStorage.getUserById(user2.getId()).getName(),
                "имя не обновилось");
    }

    @Test
    public void removeUserTest() {
        userStorage.addUser(user3);
        userStorage.deleteUser(user3);
        assertFalse(userStorage.getUsers().contains(user3), "пользователь не удалился");
    }

    @Test
    public void getUsersTest() {
        assertNotNull(userStorage.getUsers());
        assertEquals(2, userStorage.getUsers().size(), "изначально 2 пользователя");
        userStorage.addUser(user3);
        assertEquals(3, userStorage.getUsers().size(), "список не обновился");
    }

    @Test
    public void addFriendsTest() {
        service.addFriend(user1.getId(), user2.getId());

        assertEquals(userStorage.getUserById(user1.getId()).getUserFriends().getFirst(), user2.getId(),
                "пользователь не добавился в друзья");
    }

    @Test
    public void removeFriendsTest() {
        service.addFriend(user1.getId(), user2.getId());
        assertEquals(userStorage.getUserById(user1.getId()).getUserFriends().getFirst(), user2.getId(),
                "пользователь не добавился в друзья");
        service.removeFriend(user1.getId(), user2.getId());
        assertEquals(0, userStorage.getUserById(user1.getId()).getUserFriends().size(),
                "пользователь не удалился из друзей");
    }

    @Test
    public void confirmFriendTest() {
        service.addFriend(user1.getId(), user2.getId());
        service.confirmFriends(user2.getId());
        List<Long> statuses = template.queryForList("select status_id from friends where friend_id = ?",
                Long.class, user2.getId());

        for (Long id : statuses) {
            assertEquals(1, id, "статус не обновился до confirmed");
        }

    }
}
