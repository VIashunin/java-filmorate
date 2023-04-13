package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    List<User> addFriend(Integer userId, Integer friendId);

    List<User> deleteFriend(Integer userId, Integer friendId);

    List<User> getUserFriends(Integer id);

    List<User> getCommonFriends(Integer userId, Integer friendId);
}
