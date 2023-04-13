package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        String sql = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String email = rs.getString("email");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, login, name, email, birthday);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ? INTERSECT SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser1(rs), userId, friendId);
    }

    private User makeUser1(ResultSet rs) throws SQLException {
        return userStorage.getUserById(rs.getInt("friend_id"));
    }

    @Override
    public List<User> addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        if (!isFriendshipExists(userId, friendId)) {
            String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
            return getUserFriends(userId);
        } else {
            throw new ValidationException("Friendship between users " + userId + " and " + friendId + " has already existed.");
        }
    }

    @Override
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        if (isFriendshipExists(userId, friendId)) {
            String sql = "DELETE friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
            return getUserFriends(userId);
        } else {
            throw new ValidationException("Friendship between users " + userId + " and " + friendId + " does not exist.");
        }
    }

    private boolean isFriendshipExists(Integer userId, Integer friendId) {
        if (getUserFriends(userId).contains(userStorage.getUserById(friendId))) {
            return true;
        } else {
            return false;
        }
    }
}
