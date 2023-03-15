package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    int generatorId = 1;
    HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(generatorId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("User with id:{} was successfully added.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        log.info("User with id:{} was successfully updated.", user.getId());
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Users were successfully found.");
        return users.values();
    }

    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        log.info("User with id:{} was successfully found.", id);
        return users.get(id);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        log.info("User with id:{} and user with id:{} are friends.", id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
        log.info("User with id:{} and user with id:{} are not friends more.", id, friendId);
    }

    @Override
    public Collection<User> getFriends(Integer id) {
        Collection<User> friendsList = new ArrayList<>();
        for (Integer friend : users.get(id).getFriends()) {
            friendsList.add(users.get(friend));
        }
        log.info("Friends of user with id:{} were successfully found.", id);
        return friendsList;
    }

    @Override
    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        Collection<User> commonFriendsList = new ArrayList<>();
        Set<Integer> userFriends = users.get(id).getFriends();
        Set<Integer> otherUserFriends = users.get(otherId).getFriends();
        for (Integer userFriend : userFriends) {
            if (otherUserFriends.contains(userFriend)) {
                commonFriendsList.add(users.get(userFriend));
            }
        }
        log.info("Common friends of user with id:{} and user with id:{} were successfully found.", id, otherId);
        return commonFriendsList;
    }
}
