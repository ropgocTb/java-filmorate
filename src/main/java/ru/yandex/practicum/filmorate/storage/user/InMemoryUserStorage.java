package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("User added: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId()))
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        validateUser(user);
        users.put(user.getId(), user);
        log.info("User updated: {}", user);
        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (!users.containsKey(user.getId()))
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        users.remove(user.getId());
        log.info("User deleted: {}", user);
    }

    @Override
    public User getUserById(long id) {
        if (users.containsKey(id))
            return users.get(id);
        throw new NotFoundException("Пользователь с id " + id + " не найден");
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> friends1 = user.getFriends();
        Set<Long> friends2 = friend.getFriends();

        friends1.add(friendId);
        friends2.add(userId);

        user.setFriends(friends1);
        friend.setFriends(friends2);

        updateUser(user);
        updateUser(friend);
        log.info("User: {} and User: {} are now friends!", user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Set<Long> friends1 = user.getFriends();
        Set<Long> friends2 = friend.getFriends();

        friends1.remove(friendId);
        friends2.remove(userId);

        user.setFriends(friends1);
        friend.setFriends(friends2);

        updateUser(user);
        updateUser(friend);

        log.info("User: {} and User: {} are not friends anymore", user.getId(), friend.getId());
    }

    @Override
    public List<User> getUserFriends(long id) {
        User user = getUserById(id);
        return user.getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        User user1 = getUserById(id);
        User user2 = getUserById(friendId);

        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(this::getUserById)
                .toList();
    }

    @Override
    public void confirmFriends(long id) {

    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@"))
            throw new ValidationException("почта пользователя не должна быть пустой и должна содержать символ @");
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" "))
            throw new ValidationException("логин пользователя не может быть пустым и содержать пробелы");
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("UserName is empty, login used instead");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("дата рождения пользователя не может быть в будущем");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
