package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private static final String USER_FRIENDS_PATH = "/{id}/friends";
    private static final String USER_FRIEND_PATH = USER_FRIENDS_PATH + "/{friendId}";
    private static final String COMMON_FRIENDS_PATH = USER_FRIENDS_PATH + "/common/{otherId}";
    private final UserService userService;

    static {
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping("/{id}")
    public Optional<User> getById(@PathVariable long id) {
        log.debug("Запрос на получение пользователя по id = {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Запрос на получение списка пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Запрос на создание нового пользователя");
        user = userService.create(user);
        log.info("Создан новый пользователь(id = {})", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Запрос на изменение данных пользователя(id = {})", user.getId());
        user = userService.update(user);
        log.info("Данные пользователя(id = {}) изменены", user.getId());
        return user;
    }

    @PutMapping(USER_FRIEND_PATH)
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Запрос на добавления друга(user id = {}, friendId = {})", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователи добавлены в друзья(user id = {}, friendId = {})", id, friendId);
    }

    @DeleteMapping(USER_FRIEND_PATH)
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Запрос на удаление друга(user id = {}, friendId = {})", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователи удалены из друзей(user id = {}, friendId = {})", id, friendId);
    }

    @GetMapping(USER_FRIENDS_PATH)
    public Collection<User> getFriends(@PathVariable long id) {
        log.debug("Запрос на получение списка друзей(user id = {})", id);
        return userService.getFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Запрос на получение списка общих друзей(user id = {}, otherId = {})", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
