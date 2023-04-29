package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Service
public class FilmService {
    private final FilmValidator filmValidator;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    @Autowired
    public FilmService(FilmValidator filmValidator, FilmStorage filmStorage, LikesStorage likesStorage) {
        this.filmValidator = filmValidator;
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
    }

    public Film addFilm(Film film) {
        filmValidator.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidator.validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        filmValidator.countShouldBePositive(count);
        return filmStorage.getPopularFilms(count);
    }

    public Film addLike(Integer filmId, Integer userId) {
        return likesStorage.addLike(filmId, userId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        return likesStorage.deleteLike(filmId, userId);
    }
}
