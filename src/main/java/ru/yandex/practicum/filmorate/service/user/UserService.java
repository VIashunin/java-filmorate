package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserValidator userValidator;
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(UserValidator userValidator, UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userValidator = userValidator;
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public User addUser(User user) {
        userValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userValidator.validateUser(user);
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        friendsStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        friendsStorage.deleteFriend(id, friendId);
    }

    public List<User> getUserFriends(Integer id) {
        return friendsStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return friendsStorage.getCommonFriends(id, otherId);
    }
}
