package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User film);

    User updateUser(User film);

    void deleteUser(User user);

    User getUserById(long id);

    List<User> getUsers();
}
