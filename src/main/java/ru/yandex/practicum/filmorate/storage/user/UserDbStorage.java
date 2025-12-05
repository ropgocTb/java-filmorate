package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        long userId = resultSet.getLong("id");
        return User.builder()
                .id(userId)
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .userFriends(getUserFriendsIds(userId))
                .build();
    }

    private List<Long> getUserFriendsIds(long userId) {
        String sqlQuery = "select friend_id from friends where user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFriendId, userId);
    }

    private Long mapRowToFriendId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("friend_id");
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        User existingUser = getUserById(user.getId());
        String sqlQuery = "update users set " +
                "name = ?, email = ?, login = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                existingUser.getId());
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(User user) {
        User existingUser = getUserById(user.getId());
        String sqlQuery = "delete from users where id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public User getUserById(long id) {
        try {
            String sqlQuery = "select id, email, login, name, birthday from users where id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "select id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        String sqlQuery = "insert into friends (user_id, friend_id, status_id) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                friend.getId(),
                2);
        log.info("User: {} and User: {} are now friends!", user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());

        log.info("User: {} and User: {} are not friends anymore", user.getId(), friend.getId());
    }

    @Override
    public List<User> getUserFriends(long id) {
        User user = getUserById(id);
        List<User> friends = new ArrayList<>();
        for (Long userId : user.getUserFriends()) {
            friends.add(getUserById(userId));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long id, long friendId) {
        User user1 = getUserById(id);
        User user2 = getUserById(friendId);

        List<User> commonFriends = new ArrayList<>();
        Set<Long> commonSet = new HashSet<>(user1.getUserFriends());
        commonSet.retainAll(user2.getUserFriends());
        for (Long commonFriend : commonSet) {
            commonFriends.add(getUserById(commonFriend));
        }
        return commonFriends;
    }

    @Override
    public void confirmFriends(long id) {
        User user = getUserById(id);
        String sqlQuery = "update friends set status_id = 1 where friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
        log.info("All friend requests now have status confirmed for friend_id: {}", id);
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
