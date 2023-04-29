package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate, FilmStorage filmStorage, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (!isLikeExists(filmId, userId)) {
            String sql1 = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
            jdbcTemplate.update(sql1, filmId, userId);
            String sql2 = "UPDATE films SET rate = rate + 1 WHERE id = ?";
            jdbcTemplate.update(sql2, filmId);
            return filmStorage.getFilmById(filmId);
        } else {
            throw new ValidationException("Film with id:" + filmId + " already has like from user with id:" + userId + ".");
        }
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        if (isLikeExists(filmId, userId)) {
            String sql1 = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(sql1, filmId, userId);
            String sql2 = "UPDATE films SET rate = rate - 1 WHERE id = ?";
            jdbcTemplate.update(sql2, filmId);
            return filmStorage.getFilmById(filmId);
        } else {
            throw new ValidationException("Film with id:" + filmId + " does not have like from user with id:" + userId + ".");
        }
    }

    private boolean isLikeExists(Integer filmId, Integer userId) {
        String sql = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, filmId, userId);
        return likesRows.next();
    }
}
