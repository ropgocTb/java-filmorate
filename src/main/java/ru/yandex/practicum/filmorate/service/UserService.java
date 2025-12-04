package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Getter
@Setter
public class UserService {
    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (userStorage instanceof InMemoryUserStorage) {
            Set<Long> friends1 = user.getFriends();
            Set<Long> friends2 = friend.getFriends();

            friends1.add(friendId);
            friends2.add(userId);

            user.setFriends(friends1);
            friend.setFriends(friends2);

            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } else if (userStorage instanceof UserDbStorage) {
            String sqlQuery = "insert into friends (user_id, friend_id, status_id) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    user.getId(),
                    friend.getId(),
                    2);
        }
        log.info("User: {} and User: {} are now friends!", user.getId(), friend.getId());
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (userStorage instanceof InMemoryUserStorage) {
            Set<Long> friends1 = user.getFriends();
            Set<Long> friends2 = friend.getFriends();

            friends1.remove(friendId);
            friends2.remove(userId);

            user.setFriends(friends1);
            friend.setFriends(friends2);

            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } else if (userStorage instanceof UserDbStorage) {
            String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
        }
        log.info("User: {} and User: {} are not friends anymore", user.getId(), friend.getId());
    }

    public List<User> getUserFriends(long id) {
        User user = userStorage.getUserById(id);
        if (userStorage instanceof InMemoryUserStorage) {
            return user.getFriends().stream()
                    .map(userStorage::getUserById)
                    .toList();
        } else if (userStorage instanceof UserDbStorage) {
            List<User> friends = new ArrayList<>();
            for (Long userId : user.getUserFriends()) {
                friends.add(userStorage.getUserById(userId));
            }
            return friends;
        }
        return List.of();
    }

    public List<User> getCommonFriends(long id, long friendId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        if (userStorage instanceof InMemoryUserStorage) {
            return user1.getFriends().stream()
                    .filter(user2.getFriends()::contains)
                    .map(userStorage::getUserById)
                    .toList();
        } else if (userStorage instanceof UserDbStorage) {
            List<User> commonFriends = new ArrayList<>();
            Set<Long> commonSet = new HashSet<>(user1.getUserFriends());
            commonSet.retainAll(user2.getUserFriends());
            for (Long commonFriend : commonSet) {
                commonFriends.add(userStorage.getUserById(commonFriend));
            }
            return commonFriends;
        }
        return List.of();
    }

    public void confirmFriends(long id) {
        User user = userStorage.getUserById(id);
        String sqlQuery = "update friends set status_id = 1 where friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
        log.info("All friend requests now have status confirmed for friend_id: {}", id);
    }
}
