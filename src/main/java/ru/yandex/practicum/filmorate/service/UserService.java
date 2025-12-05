package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(long id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(long id, long friendId) {
        return userStorage.getCommonFriends(id, friendId);
    }

    public void confirmFriends(long id) {
        userStorage.confirmFriends(id);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }
}
