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
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable final long id) {
        return userService.getUserById(id);
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
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final long id, @PathVariable final long friendId) {
        userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/confirm")
    public void confirmFriends(@PathVariable final long id) {
        userService.confirmFriends(id);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        userService.deleteUser(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable final long id, @PathVariable final long friendId) {
        userService.removeFriend(id, friendId);
    }
}
