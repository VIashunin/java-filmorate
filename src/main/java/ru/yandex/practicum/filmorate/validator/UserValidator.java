package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserValidator {
    @Qualifier("InMemoryUserStorage")
    UserStorage userStorage;

    @Autowired
    public UserValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void userIdShouldBePositive(int id) {
        if (id <= 0) {
            log.error("User with id:{} does not exist.", id);
            throw new ItemNotFoundException("User with id:" + id + " does not exist.");
        }
    }

    public void userIdShouldExist(int id) {
        if (!userStorage.getUsersMap().containsKey(id)) {
            log.error("User with id:{} does not exist.", id);
            throw new ItemNotFoundException("User with id:" + id + " does not exist.");
        }
    }

    public void validateUser(User user) {
        List<String> errors = validateUserParams(user);
        StringBuilder str = new StringBuilder();
        if (!errors.isEmpty()) {
            for (int i = 0; i < errors.size() - 1; i++) {
                str.append(errors.get(i)).append(", ");
            }
            str.append(errors.get(errors.size() - 1)).append(".");
        }
        String result = str.toString();
        if (!result.isEmpty()) {
            log.error("When adding/updating a user, a data validation error occurred with the following values: " + result);
            throw new ValidationException("When adding/updating a user, a data validation error occurred with the following values: " + result);
        }
    }

    private List<String> validateUserParams(User user) {
        List<String> errors = new ArrayList<>();
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            errors.add("login can't be empty and shouldn't contain space");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            errors.add("email can't be empty and should contain @");
        }
        if (user.getBirthday() == null) {
            errors.add("date of birth can't be empty");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            errors.add("date of birth can't be in the future");
        }
        return errors;
    }
}
