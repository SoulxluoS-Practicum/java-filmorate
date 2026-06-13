package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    public UserController() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Запрос на получение списка пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Запрос на создание нового пользователя");
        validateEmail(user);
        validateDuplicateEmail(user);
        validateLogin(user);
        validateBirthday(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь(id = {})", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Запрос на изменение данных пользователя(id = {})", user.getId());
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя не указан");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с id = %s не найден", user.getId());
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() != null) {
            validateEmail(user);
            validateDuplicateEmail(user);
            log.debug("Изменение email пользователя(id = {}): {} -> {}", user.getId(), oldUser.getEmail(), user.getEmail());
            oldUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            validateLogin(user);
            log.debug("Изменение login пользователя(id = {}): {} -> {}", user.getId(), oldUser.getLogin(), user.getLogin());
            oldUser.setLogin(user.getLogin());
        }
        if (user.getName() != null) {
            log.debug("Изменение name пользователя(id = {}): {} -> {}", user.getId(), oldUser.getName(), user.getName());
            if (user.getName().isBlank()) {
                oldUser.setName(oldUser.getLogin());
            } else {
                oldUser.setName(user.getName());
            }
        }
        if (user.getBirthday() != null) {
            validateBirthday(user);
            log.debug("Изменение birthday пользователя(id = {}): {} -> {}", user.getId(), oldUser.getBirthday(), user.getBirthday());
            oldUser.setBirthday(user.getBirthday());
        }
        log.info("Данные пользователя(id = {}) изменены", user.getId());
        return oldUser;
    }

    private void validateEmail(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта пользователя не должна быть пустой и должна содержать @");
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пользователя не должен быть пустым и не должен содержать пробелы");
        }
    }

    private void validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения пользователя не должна быть в будущем");
        }
    }

    private void validateDuplicateEmail(User user) {
        boolean isDuplicateEmail = users.values().stream()
            .filter(user1 -> !Objects.equals(user1.getId(), user.getId()))
            .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (isDuplicateEmail) {
            throw new ValidationException("Пользователь с почтой = %s уже существует", user.getEmail());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
            .stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
        return ++currentMaxId;
    }
}
