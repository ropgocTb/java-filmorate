package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    User getUserById(long id);

    List<User> getUsers();

    void addFriend(long userId, long friendId);

    public void removeFriend(long userId, long friendId);

    public List<User> getUserFriends(long id);

    public List<User> getCommonFriends(long id, long friendId);

    public void confirmFriends(long id);
}
