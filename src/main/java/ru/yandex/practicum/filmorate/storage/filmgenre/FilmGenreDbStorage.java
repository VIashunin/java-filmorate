package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> getGenresByFilmId(Integer id) {
        String sql = "SELECT * FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return genreStorage.getGenreById(rs.getInt("genre_id"));
    }

    @Override
    public void addGenresByFilmId(Integer id, List<Genre> genreList) {
        Set<Genre> genreSet = new TreeSet<>(genreList);
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genreSet) {
            jdbcTemplate.update(sql, id, genre.getId());
        }
    }

    @Override
    public void deleteGenresByFilmId(Integer id) {
        String sql = "DELETE film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
