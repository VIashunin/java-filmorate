package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Collection<User> getUsers();

    User getUserById(Integer id);

    Map<Integer, User> getUsersMap();

    void addFriend(Integer id, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    Collection<User> getFriends(Integer id);

    Collection<User> getCommonFriends(Integer id, Integer otherId);
}
