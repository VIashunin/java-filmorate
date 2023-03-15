package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FilmValidator {
    @Qualifier("InMemoryFilmStorage")
    FilmStorage filmStorage;

    @Autowired
    public FilmValidator(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void filmIdShouldBePositive(int id) {
        if (id <= 0) {
            log.error("Film with id:{} does not exist.", id);
            throw new ItemNotFoundException("Film with id:" + id + " does not exist.");
        }
    }

    public void filmIdShouldExist(int id) {
        if (!filmStorage.getFilmsMap().containsKey(id)) {
            log.error("Film with id:{} does not exist.", id);
            throw new ItemNotFoundException("Film with id:" + id + " does not exist.");
        }
    }

    public void countShouldBePositive(int count) {
        if (count <= 0) {
            log.error("Quantity of films should be positive.");
            throw new ValidationException("Quantity of films should be positive.");
        }
    }

    public void validateFilm(Film film) {
        List<String> errors = validateFilmParams(film);
        StringBuilder str = new StringBuilder();
        if (!errors.isEmpty()) {
            for (int i = 0; i < errors.size() - 1; i++) {
                str.append(errors.get(i)).append(", ");
            }
            str.append(errors.get(errors.size() - 1)).append(".");
        }
        String result = str.toString();
        if (!result.isEmpty()) {
            log.error("When adding/updating a movie, a data validation error occurred with the following values: " + result);
            throw new ValidationException("When adding/updating a movie, a data validation error occurred with the following values: " + result);
        }
    }

    private List<String> validateFilmParams(Film film) {
        List<String> errors = new ArrayList<>();
        if (film.getName() == null || film.getName().isBlank()) {
            errors.add("name of the film can't be empty");
        }
        if (film.getDescription() == null) {
            errors.add("film should have a description");
        } else if (film.getDescription().length() > 200) {
            errors.add("description of the film can't be longer than 200 symbols");
        }
        if (film.getReleaseDate() == null) {
            errors.add("film should have a release date");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            errors.add("release date of the film can't be earlier than 28.12.1895");
        }
        if (film.getDuration() == null) {
            errors.add("film should have a duration");
        } else if (film.getDuration() <= 0) {
            errors.add("duration of the film should be positive");
        }
        return errors;
    }
}
