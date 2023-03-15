package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    FilmController filmController = new FilmController(new FilmService(new FilmValidator(inMemoryFilmStorage)
            , new UserValidator(new InMemoryUserStorage()), inMemoryFilmStorage));

    @Test
    public void testAddNormalFilm() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Movie Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        filmController.addFilm(film);
        assertEquals("[Film(id=1, name=Movie, description=Movie Description, releaseDate=2000-01-01, duration=60, likes=[], rate=null)]"
                , filmController.getFilms().toString());
    }

    @Test
    public void testAddWrongFilm() {
        Film film = new Film();
        film.setName("   ");
        film.setDescription("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        film.setReleaseDate(LocalDate.of(1500, 1, 1));
        film.setDuration(-5);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertEquals("When adding/updating a movie, a data validation error occurred with the following values: name of the film can't be empty, " +
                "description of the film can't be longer than 200 symbols, release date of the film can't be earlier than 28.12.1895, " +
                "duration of the film should be positive.", exception.getMessage());
    }

    @Test
    public void testAddEmptyFilm() {
        Film film = new Film();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        assertEquals("When adding/updating a movie, a data validation error occurred with the following values: name of the film can't be empty, " +
                "film should have a description, film should have a release date, film should have a duration.", exception.getMessage());
    }

    @Test
    public void testUpdateNormalFilm() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Movie Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        filmController.addFilm(film);
        assertEquals("[Film(id=1, name=Movie, description=Movie Description, releaseDate=2000-01-01, duration=60, likes=[], rate=null)]"
                , filmController.getFilms().toString());
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Movie - 1");
        film1.setDescription("Movie Description - 1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(61);
        filmController.updateFilm(film1);
        assertEquals("[Film(id=1, name=Movie - 1, description=Movie Description - 1, releaseDate=2001-01-01, duration=61, likes=[], rate=null)]"
                , filmController.getFilms().toString());
    }

    @Test
    public void testUpdateWrongFilm() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Movie Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(60);
        filmController.addFilm(film);
        assertEquals("[Film(id=1, name=Movie, description=Movie Description, releaseDate=2000-01-01, duration=60, likes=[], rate=null)]"
                , filmController.getFilms().toString());
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("   ");
        film1.setDescription("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        film1.setReleaseDate(LocalDate.of(1500, 1, 1));
        film1.setDuration(-5);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(film1)
        );
        assertEquals("When adding/updating a movie, a data validation error occurred with the following values: name of the film can't be empty, " +
                "description of the film can't be longer than 200 symbols, release date of the film can't be earlier than 28.12.1895, " +
                "duration of the film should be positive.", exception.getMessage());
        Film film2 = new Film();
        film2.setId(999);
        ItemNotFoundException exception1 = assertThrows(
                ItemNotFoundException.class,
                () -> filmController.updateFilm(film2)
        );
        assertEquals("Film with id:999 does not exist.", exception1.getMessage());
    }

    @Test
    public void testUpdateEmptyFilm() {
        Film film = new Film();
        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> filmController.updateFilm(film)
        );
        assertEquals("Film with id:0 does not exist.", exception.getMessage());
    }
}