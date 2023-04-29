package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    private void initialization(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(List.of());
        }
        if (film.getRate() == null) {
            film.setRate(0);
        }
    }

    @Override
    public Film addFilm(Film film) {
        initialization(film);
        String sql = "INSERT INTO films (name, description, release_date, duration, rate, mpa) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        if (!film.getGenres().isEmpty()) {
            filmGenreStorage.addGenresByFilmId(film.getId(), film.getGenres());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        initialization(film);
        getFilmById(film.getId());
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        if (film.getGenres().isEmpty()) {
            filmGenreStorage.deleteGenresByFilmId(film.getId());
        } else {
            filmGenreStorage.deleteGenresByFilmId(film.getId());
            filmGenreStorage.addGenresByFilmId(film.getId(), film.getGenres());
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer rate = rs.getInt("rate");
        Mpa mpa = mpaStorage.getMpaById(rs.getInt("mpa"));
        List<Genre> genres = filmGenreStorage.getGenresByFilmId(rs.getInt("id"));
        return new Film(id, name, description, releaseDate, duration, rate, mpa, genres);
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Integer filmId = filmRows.getInt("id");
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate releaseDate = filmRows.getDate("release_date").toLocalDate();
            Integer duration = filmRows.getInt("duration");
            Integer rate = filmRows.getInt("rate");
            Mpa mpa = mpaStorage.getMpaById(filmRows.getInt("mpa"));
            List<Genre> genres = filmGenreStorage.getGenresByFilmId(filmRows.getInt("id"));
            log.info("Film with id:{} was found.", filmId);
            return new Film(filmId, name, description, releaseDate, duration, rate, mpa, genres);
        } else {
            log.info("Film with id:{} was not found.", id);
            throw new ItemNotFoundException("Film with id:" + id + " was not found.");
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT * FROM films ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }
}
