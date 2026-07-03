package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserController userController = new UserController(userStorage, new UserService(userStorage));

    @Test
    void createUser() {
        User userValid = User.builder()
            .login("LoginValid")
            .name("Name Valid")
            .email("emailvalid@yandex.ru")
            .birthday(LocalDate.parse("2000-07-05"))
            .build();
        userController.create(userValid);
        assertTrue(userController.getAll().contains(userValid), "Добавление корректного userValid не прошло валидацию");

        User userFailEmailDuplicate = userValid.toBuilder()
            .id(null)
            .build();
        try {
            userController.create(userFailEmailDuplicate);
        } catch (ValidationException ignored) {
        }
        assertTrue(userController.getAll().contains(userValid), "Некорректная валидация userFailEmailDuplicate: дубликат email не должен проходить");

        User userValidNullName = userValid.toBuilder()
            .id(null)
            .name(null)
            .email("emailvalidnullname@yandex.ru").build();
        userController.create(userValidNullName);
        assertEquals(userValidNullName.getName(), userValidNullName.getLogin(), "Null name должен заменяться на login");

        User userValidEmptyName = userValid.toBuilder()
            .id(null)
            .name("")
            .email("emailvalidemptyname@yandex.ru")
            .build();
        userController.create(userValidEmptyName);
        assertEquals(userValidEmptyName.getName(), userValidEmptyName.getLogin(), "Пустой name должен заменяться на login");

        User userFailEmail = userValid.toBuilder().id(null).email("").build();
        try {
            userController.create(userFailEmail);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailEmail), "Некорректная валидация userFailEmail: пустой email не должен проходить");

        User userFailEmail2 = userValid.toBuilder()
            .id(null)
            .email("emailfailyandex.ru")
            .build();
        try {
            userController.create(userFailEmail2);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailEmail2), "Некорректная валидация userFailEmail2: email без @ не должен проходить");

        User userFailLogin = userValid.toBuilder()
            .id(null)
            .email("emailfaillogin@yandex.ru")
            .login("")
            .build();
        try {
            userController.create(userFailLogin);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailLogin), "Некорректная валидация userFailLogin: пустой login не должен проходить");

        User userFailLogin2 = userValid.toBuilder()
            .id(null)
            .email("emailfaillogin2@yandex.ru")
            .login("Petr Grey")
            .build();
        try {
            userController.create(userFailLogin2);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailLogin2), "Некорректная валидация userFailLogin2: login с пробелами не должен проходить");

        String invalidBirthday = LocalDate.now().plusYears(1)
            .atStartOfDay(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        User userFailBirthday = userValid.toBuilder()
            .id(null)
            .email("emailfailbirthday@yandex.ru")
            .birthday(LocalDate.parse(invalidBirthday))
            .build();
        try {
            userController.create(userFailBirthday);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailBirthday), "Некорректная валидация userFailBirthday: birthday в будущем не должен проходить");
    }

    @Test
    void updateUser() {
        User userEmpty = User.builder().build();
        try {
            userController.update(userEmpty);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userEmpty), "Пустой UserEmpty не должен быть обновлен");

        User userFailId = User.builder().id(-1L).name("UserUnknown").build();
        try {
            userController.update(userFailId);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getAll().contains(userFailId), "UserFailId с не корректным Id не должен быть обновлен");

        User userValid = User.builder()
            .login("LoginValid")
            .name("Name Valid")
            .email("emailvalid@yandex.ru")
            .birthday(LocalDate.parse("2000-07-05"))
            .build();
        userController.create(userValid);

        User userValidUpdate = userValid.toBuilder()
            .login("LoginValidUpdate")
            .name("Name Valid Update")
            .email("emailvalidupdate@yandex.ru")
            .birthday(LocalDate.parse("2014-07-05"))
            .build();
        userController.update(userValidUpdate);
        assertEquals(userValid, userValidUpdate, "Корректное обновление userValidUpdate не прошло валидацию");

        User userValidUpdateName = userValid.toBuilder()
            .name("")
            .build();
        userController.update(userValidUpdateName);
        assertEquals(userValid.getName(), userValid.getLogin(), "Пустой name должен заменяться на login");

        User userFailUpdate = userValid.toBuilder()
            .login("Login Fail Update")
            .name("Name Fail Update")
            .email("")
            .birthday(LocalDate.parse("2030-07-05"))
            .build();
        try {
            userController.update(userFailUpdate);
        } catch (ValidationException ignored) {
        }
        assertNotEquals(userValid, userFailUpdate, "Некорректное обновление userFailUpdate прошло валидацию");
    }
}