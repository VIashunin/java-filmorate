package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    int generatorId = 1;
    HashMap<Integer, Film> films = new HashMap<>();
    FilmValidator filmValidator = new FilmValidator();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        String validation = filmValidator.validateFilm(film);
        if (validation.isEmpty()) {
            film.setId(generatorId++);
            films.put(film.getId(), film);
            log.info("Film with id:{} was successfully added.", film.getId());
            return film;
        } else {
            log.warn("When adding a movie, a data validation error occurred with the following values: " + validation);
            throw new ValidationException("When adding a movie, a data validation error occurred with the following values: " + validation);
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == 0) {
            log.warn("Film doesn't have an id.");
            throw new ValidationException("Film doesn't have an id.");
        } else {
            if (films.containsKey(film.getId())) {
                String validation = filmValidator.validateFilm(film);
                if (validation.isEmpty()) {
                    films.put(film.getId(), film);
                    log.info("Film with id:{} was successfully updated.", film.getId());
                    return film;
                } else {
                    log.warn("When updating a movie, a data validation error occurred with the following values: " + validation);
                    throw new ValidationException("When updating a movie, a data validation error occurred with the following values: " + validation);
                }
            } else {
                log.warn("Film with id:{} does not exist.", film.getId());
                throw new ValidationException("Film with id:" + film.getId() + " does not exist.");
            }
        }
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }
}
