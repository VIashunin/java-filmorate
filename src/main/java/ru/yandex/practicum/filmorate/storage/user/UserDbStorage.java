package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void initialization(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User addUser(User user) {
        initialization(user);
        String sql = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        initialization(user);
        getUserById(user.getId());
        String sql = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    ;

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            Integer userId = userRows.getInt("id");
            String userLogin = userRows.getString("login");
            String userName = userRows.getString("name");
            String userEmail = userRows.getString("email");
            LocalDate userBirthday = userRows.getDate("birthday").toLocalDate();
            log.info("User with id:{} was found.", userId);
            return new User(userId, userLogin, userName, userEmail, userBirthday);
        } else {
            log.info("User with id:{} was not found.", id);
            throw new ItemNotFoundException("User with id:" + id + " was not found.");
        }
    }

    ;

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String login = rs.getString("login");
        String name = rs.getString("name");
        String email = rs.getString("email");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, login, name, email, birthday);
    }
}
