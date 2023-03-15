package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    UserValidator userValidator;
    @Qualifier("InMemoryUserStorage")
    UserStorage userStorage;

    @Autowired
    public UserService(UserValidator userValidator, UserStorage userStorage) {
        this.userValidator = userValidator;
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        userValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userValidator.userIdShouldBePositive(user.getId());
        userValidator.userIdShouldExist(user.getId());
        userValidator.validateUser(user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        userValidator.userIdShouldBePositive(id);
        userValidator.userIdShouldExist(id);
        return userStorage.getUserById(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        userValidator.userIdShouldBePositive(id);
        userValidator.userIdShouldExist(id);
        userValidator.userIdShouldBePositive(friendId);
        userValidator.userIdShouldExist(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userValidator.userIdShouldBePositive(id);
        userValidator.userIdShouldExist(id);
        userValidator.userIdShouldBePositive(friendId);
        userValidator.userIdShouldExist(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(Integer id) {
        userValidator.userIdShouldBePositive(id);
        userValidator.userIdShouldExist(id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        userValidator.userIdShouldBePositive(id);
        userValidator.userIdShouldExist(id);
        userValidator.userIdShouldBePositive(otherId);
        userValidator.userIdShouldExist(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }
}
