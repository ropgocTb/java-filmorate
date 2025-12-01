package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> friends1 = user.getFriends();
        Set<Long> friends2 = friend.getFriends();

        friends1.add(friendId);
        friends2.add(userId);

        user.setFriends(friends1);
        friend.setFriends(friends2);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.info("User: {} and User: {} are now friends!", user.getId(), friend.getId());
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> friends1 = user.getFriends();
        Set<Long> friends2 = friend.getFriends();

        friends1.remove(friendId);
        friends2.remove(userId);

        user.setFriends(friends1);
        friend.setFriends(friends2);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("User: {} and User: {} are not friends anymore", user.getId(), friend.getId());
    }

    public List<User> getUserFriends(long id) {
        User user = userStorage.getUserById(id);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(long id, long friendId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUserById)
                .toList();
    }
}
