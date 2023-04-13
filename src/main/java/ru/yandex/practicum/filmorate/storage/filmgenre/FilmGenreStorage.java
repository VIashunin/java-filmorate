package ru.yandex.practicum.filmorate.storage.filmgenre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<Genre> getGenresByFilmId(Integer id);

    void addGenresByFilmId(Integer id, List<Genre> genreList);

    void deleteGenresByFilmId(Integer id);
}
