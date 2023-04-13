package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;

public interface LikesStorage {

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);
}
