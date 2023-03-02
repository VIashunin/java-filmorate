package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();

    Film getFilmById(Integer id);

    Map<Integer, Film> getFilmsMap();

    void addLike (Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    Collection<Film> getPopularFilms(Integer count);
}
