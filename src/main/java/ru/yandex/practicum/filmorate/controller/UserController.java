package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Запрос на получение списка пользователей");
        return userStorage.getUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Запрос на создание нового пользователя");
        user = userStorage.createUser(user);
        log.info("Создан новый пользователь(id = {})", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Запрос на изменение данных пользователя(id = {})", user.getId());
        user = userStorage.updateUser(user);
        log.info("Данные пользователя(id = {}) изменены", user.getId());
        return user;
    }
}
