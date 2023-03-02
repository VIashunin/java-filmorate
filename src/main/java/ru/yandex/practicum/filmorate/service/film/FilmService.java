package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    FilmValidator filmValidator;
    @Qualifier("InMemoryFilmStorage")
    FilmStorage filmStorage;
    @Qualifier("InMemoryUserStorage")
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmValidator filmValidator, FilmStorage filmStorage, UserStorage userStorage) {
        this.filmValidator = filmValidator;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        String validation = filmValidator.validateFilm(film);
        if (validation.isEmpty()) {
            return filmStorage.addFilm(film);
        } else {
            log.error("When adding a movie, a data validation error occurred with the following values: " + validation);
            throw new ValidationException("When adding a movie, a data validation error occurred with the following values: " + validation);
        }
    }

    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            log.error("Film with id:{} does not exist.", film.getId());
            throw new ItemNotFoundException("Film with id:" + film.getId() + " does not exist.");
        } else {
            if (filmStorage.getFilmsMap().containsKey(film.getId())) {
                String validation = filmValidator.validateFilm(film);
                if (validation.isEmpty()) {
                    return filmStorage.updateFilm(film);
                } else {
                    log.error("When updating a movie, a data validation error occurred with the following values: " + validation);
                    throw new ValidationException("When updating a movie, a data validation error occurred with the following values: " + validation);
                }
            } else {
                log.error("Film with id:{} does not exist.", film.getId());
                throw new ItemNotFoundException("Film with id:" + film.getId() + " does not exist.");
            }
        }
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        if (id <= 0 || !filmStorage.getFilmsMap().containsKey(id)) {
            log.error("Film with id:{} does not exist.", id);
            throw new ItemNotFoundException("Film with id:" + id + " does not exist.");
        }
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0 || !filmStorage.getFilmsMap().containsKey(id)
                || !userStorage.getUsersMap().containsKey(userId)) {
            log.error("Film with id:{} and/or user with id:{} do/does not exist.", id, userId);
            throw new ItemNotFoundException("Film with id:" + id + " and/or user with id:" + userId + " do/does not exist.");
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (id <= 0 || userId <= 0 || !filmStorage.getFilmsMap().containsKey(id)
                || !userStorage.getUsersMap().containsKey(userId)) {
            log.error("Film with id:{} and/or user with id:{} do/does not exist.", id, userId);
            throw new ItemNotFoundException("Film with id:" + id + " and/or user with id:" + userId + " do/does not exist.");
        }
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        if (count <= 0) {
            log.error("Quantity of films should be positive.");
            throw new ValidationException("Quantity of films should be positive.");
        }
        return filmStorage.getPopularFilms(count);
    }

}
