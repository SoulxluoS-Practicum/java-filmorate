package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping("/{id}")
    public Optional<User> getById(@PathVariable long id) {
        log.debug("Запрос на получение пользователя по id = {}", id);
        return userStorage.getById(id);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Запрос на получение списка пользователей");
        return userStorage.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Запрос на создание нового пользователя");
        user = userStorage.create(user);
        log.info("Создан новый пользователь(id = {})", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Запрос на изменение данных пользователя(id = {})", user.getId());
        user = userStorage.update(user);
        log.info("Данные пользователя(id = {}) изменены", user.getId());
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Запрос на добавления друга(user id = {}, friendId = {})", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователи добавлены в друзья(user id = {}, friendId = {})", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Запрос на удаление друга(user id = {}, friendId = {})", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователи удалены из друзей(user id = {}, friendId = {})", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.debug("Запрос на получение списка друзей(user id = {})", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Запрос на получение списка общих друзей(user id = {}, otherId = {})", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
