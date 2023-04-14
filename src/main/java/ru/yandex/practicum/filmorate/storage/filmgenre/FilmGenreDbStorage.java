package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        List<Genre> uniqueGenres = new ArrayList<>(genreSet);
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new GenresAddBatch(id, uniqueGenres));
    }

    @Override
    public void deleteGenresByFilmId(Integer id) {
        String sql = "DELETE film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class GenresAddBatch implements BatchPreparedStatementSetter {
        private final List<Genre> genres;
        private final Integer filmId;

        public GenresAddBatch(Integer filmId, final List<Genre> genres) {
            this.filmId = filmId;
            this.genres = genres;
        }

        @Override
        public final void setValues(
                final PreparedStatement ps,
                final int i) throws SQLException {
            ps.setInt(1, filmId);
            ps.setInt(2, genres.get(i).getId());
        }

        @Override
        public int getBatchSize() {
            return genres.size();
        }
    }
}
