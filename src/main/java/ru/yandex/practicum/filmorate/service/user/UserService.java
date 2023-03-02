package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        String validation = userValidator.validateUser(user);
        if (validation.isEmpty()) {
           return userStorage.addUser(user);
        } else {
            log.error("When adding a user, a data validation error occurred with the following values: " + validation);
            throw new ValidationException("When adding a user, a data validation error occurred with the following values: " + validation);
        }
    }

    public User updateUser(User user) {
        if (user.getId() <= 0) {
            log.error("User with id:{} does not exist.", user.getId());
            throw new ItemNotFoundException("User with id:" + user.getId() + " does not exist.");
        } else {
            if (userStorage.getUsersMap().containsKey(user.getId())) {
                String validation = userValidator.validateUser(user);
                if (validation.isEmpty()) {
                    return userStorage.updateUser(user);
                } else {
                    log.error("When updating a user, a data validation error occurred with the following values: " + validation);
                    throw new ValidationException("When updating a user, a data validation error occurred with the following values: " + validation);
                }
            } else {
                log.error("User with id:{} does not exist.", user.getId());
                throw new ItemNotFoundException("User with id:" + user.getId() + " does not exist.");
            }
        }
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        if (id <= 0 || !userStorage.getUsersMap().containsKey(id)) {
            log.error("User with id:{} does not exist.", id);
            throw new ItemNotFoundException("User with id:" + id + " does not exist.");
        }
        return userStorage.getUserById(id);
    }

    public void addFriend(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0 || !userStorage.getUsersMap().containsKey(id)
                || !userStorage.getUsersMap().containsKey(friendId)) {
            log.error("User with id:{} and/or user with id:{} do/does not exist.", id, friendId);
            throw new ItemNotFoundException("User with id:" + id + " and/or user with id:" + friendId + " do/does not exist.");
        }
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0 || !userStorage.getUsersMap().containsKey(id)
                || !userStorage.getUsersMap().containsKey(friendId)) {
            log.error("User with id:{} and/or user with id:{} do/does not exist.", id, friendId);
            throw new ItemNotFoundException("User with id:" + id + " and/or user with id:" + friendId + " do/does not exist.");
        }
        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(Integer id) {
        if (id <= 0 || !userStorage.getUsersMap().containsKey(id)) {
            log.error("User with id:{} does not exist.", id);
            throw new ItemNotFoundException("User with id:" + id + " does not exist.");
        }
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        if (id <= 0 || otherId <= 0 || !userStorage.getUsersMap().containsKey(id)
                || !userStorage.getUsersMap().containsKey(otherId)) {
            log.error("User with id:{} and/or user with id:{} do/does not exist.", id, otherId);
            throw new ItemNotFoundException("User with id:" + id + " and/or user with id:" + otherId + " do/does not exist.");
        }
        return userStorage.getCommonFriends(id, otherId);
    }
}
