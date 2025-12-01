package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired private final UserStorage userStorage;

    public void addFriend(long id1, long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);

        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        friends1.add(user2.getId());
        friends2.add(user1.getId());

        user1.setFriends(friends1);
        user2.setFriends(friends2);

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
        log.info("User: {} and User: {} are now friends!", user1.getId(), user2.getId());
    }

    public void removeFriend(long id1, long id2) {
        User user1 = userStorage.getUserById(id1);
        User user2 = userStorage.getUserById(id2);

        Set<Long> friends1 = user1.getFriends();
        Set<Long> friends2 = user2.getFriends();

        friends1.remove(user2.getId());
        friends2.remove(user1.getId());

        user1.setFriends(friends1);
        user2.setFriends(friends2);

        userStorage.updateUser(user1);
        userStorage.updateUser(user2);

        log.info("User: {} and User: {} are not friends anymore", user1.getId(), user2.getId());
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
