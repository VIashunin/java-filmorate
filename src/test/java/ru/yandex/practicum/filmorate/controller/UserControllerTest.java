package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController = new UserController(new UserService(new UserValidator(), new InMemoryUserStorage()));

    @Test
    public void testAddNormalUser1() {
        User user = new User();
        user.setLogin("user");
        user.setName("user name");
        user.setEmail("1@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.addUser(user);
        assertEquals("[User(id=1, login=user, name=user name, email=1@gmail.com, birthday=2000-01-01, friends=[])]"
                , userController.getUsers().toString());
    }

    @Test
    public void testAddNormalUser2() {
        User user = new User();
        user.setLogin("user");
        user.setEmail("1@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.addUser(user);
        assertEquals("[User(id=1, login=user, name=user, email=1@gmail.com, birthday=2000-01-01, friends=[])]"
                , userController.getUsers().toString());
    }

    @Test
    public void testAddWrongUser() {
        User user = new User();
        user.setLogin("us er");
        user.setEmail("1gmail.com");
        user.setBirthday(LocalDate.of(2025, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );
        assertEquals("When adding a user, a data validation error occurred with the following values: " +
                "login can't be empty and shouldn't contain space, email can't be empty and should contain @, " +
                "date of birth can't be in the future.", exception.getMessage());
    }

    @Test
    public void testAddEmptyUser() {
        User user = new User();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );
        assertEquals("When adding a user, a data validation error occurred with the following values: " +
                "login can't be empty and shouldn't contain space, email can't be empty and should contain @, " +
                "date of birth can't be empty.", exception.getMessage());
    }

    @Test
    public void testUpdateNormalUser() {
        User user = new User();
        user.setLogin("user");
        user.setName("user name");
        user.setEmail("1@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.addUser(user);
        assertEquals("[User(id=1, login=user, name=user name, email=1@gmail.com, birthday=2000-01-01, friends=[])]"
                , userController.getUsers().toString());
        User user1 = new User();
        user1.setId(1);
        user1.setLogin("user1");
        user1.setEmail("333@mail.ru");
        user1.setBirthday(LocalDate.of(2001, 1, 1));
        userController.updateUser(user1);
        assertEquals("[User(id=1, login=user1, name=user1, email=333@mail.ru, birthday=2001-01-01, friends=[])]"
                , userController.getUsers().toString());
    }

    @Test
    public void testUpdateWrongUser() {
        User user = new User();
        user.setLogin("user");
        user.setName("user name");
        user.setEmail("1@gmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.addUser(user);
        assertEquals("[User(id=1, login=user, name=user name, email=1@gmail.com, birthday=2000-01-01, friends=[])]"
                , userController.getUsers().toString());
        User user1 = new User();
        user1.setId(1);
        user1.setLogin("   ");
        user1.setName("user");
        user1.setEmail("mail.ru");
        user1.setBirthday(LocalDate.of(2025, 1, 1));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(user1)
        );
        assertEquals("When updating a user, a data validation error occurred with the following values: " +
                "login can't be empty and shouldn't contain space, email can't be empty and should contain @, " +
                "date of birth can't be in the future.", exception.getMessage());
        User user2 = new User();
        user2.setId(999);
        ItemNotFoundException exception1 = assertThrows(
                ItemNotFoundException.class,
                () -> userController.updateUser(user2)
        );
        assertEquals("User with id:999 does not exist.", exception1.getMessage());
    }

    @Test
    public void testUpdateEmptyUser() {
        User user = new User();
        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> userController.updateUser(user)
        );
        assertEquals("User with id:0 does not exist.", exception.getMessage());
    }
}