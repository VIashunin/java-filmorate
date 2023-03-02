package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmValidator {

    public String validateFilm(Film film) {
        List<String> errors = validateFilmParams(film);
        StringBuilder str = new StringBuilder();
        if (!errors.isEmpty()) {
            for (int i = 0; i < errors.size() - 1; i++) {
                str.append(errors.get(i)).append(", ");
            }
            str.append(errors.get(errors.size() - 1)).append(".");
        }
        return str.toString();
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
