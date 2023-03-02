package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserValidator {

    public String validateUser(User user) {
        List<String> errors = validateUserParams(user);
        StringBuilder str = new StringBuilder();
        if (!errors.isEmpty()) {
            for (int i = 0; i < errors.size() - 1; i++) {
                str.append(errors.get(i)).append(", ");
            }
            str.append(errors.get(errors.size() - 1)).append(".");
        }
        return str.toString();
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
