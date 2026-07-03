package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getFriends(long userId) {
        User user = userStorage.getById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(userId)));
        return user.getFriends().isEmpty() ?
            Collections.emptyList() :
            userStorage.getAll().stream()
                .filter(user1 -> user.getFriends().contains(user1.getId()))
                .toList();
    }

    public Collection<User> getCommonFriends(long userId, long otherID) {
        User user = userStorage.getById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(userId)));
        User otherUser = userStorage.getById(otherID)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(otherID)));
        return user.getFriends().isEmpty() || otherUser.getFriends().isEmpty() ?
            Collections.emptyList() :
            userStorage.getAll().stream()
                .filter(user1 -> user.getFriends().contains(user1.getId()) && otherUser.getFriends().contains(user1.getId()))
                .toList();
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            return;
        }
        User user = userStorage.getById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(userId)));
        User userFriend = userStorage.getById(friendId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(friendId)));
        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            return;
        }
        User user = userStorage.getById(userId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(userId)));
        User userFriend = userStorage.getById(friendId)
            .orElseThrow(() -> new NotFoundException("Пользователь id = %s не найден".formatted(friendId)));
        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(userId);
    }

}
