package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUserStorage().getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable final long id) {
        return userService.getUserStorage().getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable final long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable final long id, @PathVariable final long friendId) {
        return userService.getCommonFriends(id, friendId);
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.getUserStorage().addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.getUserStorage().updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final long id, @PathVariable final long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        userService.getUserStorage().deleteUser(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable final long id, @PathVariable final long friendId) {
        userService.removeFriend(id, friendId);
    }
}
