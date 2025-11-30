package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    @PostMapping
    public User addUser(@RequestBody User user) {
        try {
            validateUser(user);
            user.setId(++id);
            users.put(id, user);
            log.info("User added: {}", user);
        } catch (ValidationException ex) {
            log.error("Validation error: {}", ex.getMessage());
            throw new RuntimeException();
        }
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId()))
            throw new RuntimeException();
        try {
            validateUser(user);
            users.put(user.getId(), user);
            log.info("User updated: {}", user);
        } catch (ValidationException ex) {
            log.error("Validation error: {}", ex.getMessage());
            throw new RuntimeException();
        }
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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
}
