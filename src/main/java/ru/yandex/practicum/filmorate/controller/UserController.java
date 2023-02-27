package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    int generatorId = 1;
    HashMap<Integer, User> users = new HashMap<>();
    UserValidator userValidator = new UserValidator();

    @PostMapping
    public User addUser(@RequestBody User user) {
        String validation = userValidator.validateUser(user);
        if (validation.isEmpty()) {
            user.setId(generatorId++);
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("User with id:{} was successfully added.", user.getId());
            return user;
        } else {
            log.error("When adding a user, a data validation error occurred with the following values: " + validation);
            throw new ValidationException("When adding a user, a data validation error occurred with the following values: " + validation);
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == 0) {
            log.error("User doesn't have an id.");
            throw new ValidationException("User doesn't have an id.");
        } else {
            if (users.containsKey(user.getId())) {
                String validation = userValidator.validateUser(user);
                if (validation.isEmpty()) {
                    if (user.getName() == null) {
                        user.setName(user.getLogin());
                    }
                    users.put(user.getId(), user);
                    log.info("User with id:{} was successfully updated.", user.getId());
                    return user;
                } else {
                    log.error("When updating a user, a data validation error occurred with the following values: " + validation);
                    throw new ValidationException("When updating a user, a data validation error occurred with the following values: " + validation);
                }
            } else {
                log.error("User with id:{} does not exist.", user.getId());
                throw new ValidationException("User with id:" + user.getId() + " does not exist.");
            }
        }
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }
}
