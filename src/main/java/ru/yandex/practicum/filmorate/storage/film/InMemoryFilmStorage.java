package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    int generatorId = 1;
    HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(generatorId++);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        log.info("Film with id:{} was successfully added.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        log.info("Film with id:{} was successfully updated.", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Films were successfully found.");
        return films.values();
    }

    @Override
    public Film getFilmById(Integer id) {
        log.info("Film with id:{} was successfully found.", id);
        return films.get(id);
    }

    @Override
    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        films.get(id).getLikes().add(userId);
        log.info("User with id:{} liked the film with id:{}", userId, id);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        films.get(id).getLikes().remove(userId);
        log.info("User with id:{} removed like of the film with id:{}", userId, id);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        log.info("{} popular films was/were successfully found.", count);
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getLikesQuantity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
