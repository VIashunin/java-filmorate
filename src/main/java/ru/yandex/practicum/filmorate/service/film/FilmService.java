package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    FilmValidator filmValidator;
    UserValidator userValidator;
    @Qualifier("InMemoryFilmStorage")
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmValidator filmValidator, UserValidator userValidator, FilmStorage filmStorage) {
        this.filmValidator = filmValidator;
        this.userValidator = userValidator;
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        filmValidator.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidator.filmIdShouldBePositive(film.getId());
        filmValidator.filmIdShouldExist(film.getId());
        filmValidator.validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        filmValidator.filmIdShouldBePositive(id);
        filmValidator.filmIdShouldExist(id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer id, Integer userId) {
        filmValidator.filmIdShouldBePositive(id);
        filmValidator.filmIdShouldExist(id);
        //userValidator.userIdShouldBePositive(userId);
        //userValidator.userIdShouldExist(userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmValidator.filmIdShouldBePositive(id);
        filmValidator.filmIdShouldExist(id);
        userValidator.userIdShouldBePositive(userId);
        userValidator.userIdShouldExist(userId);
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        filmValidator.countShouldBePositive(count);
        return filmStorage.getPopularFilms(count);
    }
}
