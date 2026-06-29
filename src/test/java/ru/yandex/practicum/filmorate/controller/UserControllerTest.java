package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserController userController = new UserController();

    @Test
    void createUser() {
        User userValid = User.builder()
            .login("LoginValid")
            .name("Name Valid")
            .email("emailvalid@yandex.ru")
            .birthday(LocalDate.parse("2000-07-05"))
            .build();
        userController.createUser(userValid);
        assertTrue(userController.getUsers().contains(userValid), "Добавление корректного userValid не прошло валидацию");

        User userFailEmailDuplicate = userValid.toBuilder()
            .id(null)
            .build();
        try {
            userController.createUser(userFailEmailDuplicate);
        } catch (ValidationException ignored) {
        }
        assertTrue(userController.getUsers().contains(userValid), "Некорректная валидация userFailEmailDuplicate: дубликат email не должен проходить");

        User userValidNullName = userValid.toBuilder()
            .id(null)
            .name(null)
            .email("emailvalidnullname@yandex.ru").build();
        userController.createUser(userValidNullName);
        assertEquals(userValidNullName.getName(), userValidNullName.getLogin(), "Null name должен заменяться на login");

        User userValidEmptyName = userValid.toBuilder()
            .id(null)
            .name("")
            .email("emailvalidemptyname@yandex.ru")
            .build();
        userController.createUser(userValidEmptyName);
        assertEquals(userValidEmptyName.getName(), userValidEmptyName.getLogin(), "Пустой name должен заменяться на login");

        User userFailEmail = userValid.toBuilder().id(null).email("").build();
        try {
            userController.createUser(userFailEmail);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailEmail), "Некорректная валидация userFailEmail: пустой email не должен проходить");

        User userFailEmail2 = userValid.toBuilder()
            .id(null)
            .email("emailfailyandex.ru")
            .build();
        try {
            userController.createUser(userFailEmail2);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailEmail2), "Некорректная валидация userFailEmail2: email без @ не должен проходить");

        User userFailLogin = userValid.toBuilder()
            .id(null)
            .email("emailfaillogin@yandex.ru")
            .login("")
            .build();
        try {
            userController.createUser(userFailLogin);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailLogin), "Некорректная валидация userFailLogin: пустой login не должен проходить");

        User userFailLogin2 = userValid.toBuilder()
            .id(null)
            .email("emailfaillogin2@yandex.ru")
            .login("Petr Grey")
            .build();
        try {
            userController.createUser(userFailLogin2);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailLogin2), "Некорректная валидация userFailLogin2: login с пробелами не должен проходить");

        String invalidBirthday = LocalDate.now().plusYears(1)
            .atStartOfDay(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        User userFailBirthday = userValid.toBuilder()
            .id(null)
            .email("emailfailbirthday@yandex.ru")
            .birthday(LocalDate.parse(invalidBirthday))
            .build();
        try {
            userController.createUser(userFailBirthday);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailBirthday), "Некорректная валидация userFailBirthday: birthday в будущем не должен проходить");
    }

    @Test
    void updateUser() {
        User userEmpty = User.builder().build();
        try {
            userController.updateUser(userEmpty);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userEmpty), "Пустой UserEmpty не должен быть обновлен");

        User userFailId = User.builder().id(-1L).name("UserUnknown").build();
        try {
            userController.updateUser(userFailId);
        } catch (ValidationException ignored) {
        }
        assertFalse(userController.getUsers().contains(userFailId), "UserFailId с не корректным Id не должен быть обновлен");

        User userValid = User.builder()
            .login("LoginValid")
            .name("Name Valid")
            .email("emailvalid@yandex.ru")
            .birthday(LocalDate.parse("2000-07-05"))
            .build();
        userController.createUser(userValid);

        User userValidUpdate = userValid.toBuilder()
            .login("LoginValidUpdate")
            .name("Name Valid Update")
            .email("emailvalidupdate@yandex.ru")
            .birthday(LocalDate.parse("2014-07-05"))
            .build();
        userController.updateUser(userValidUpdate);
        assertEquals(userValid, userValidUpdate, "Корректное обновление userValidUpdate не прошло валидацию");

        User userValidUpdateName = userValid.toBuilder()
            .name("")
            .build();
        userController.updateUser(userValidUpdateName);
        assertEquals(userValid.getName(), userValid.getLogin(), "Пустой name должен заменяться на login");

        User userFailUpdate = userValid.toBuilder()
            .login("Login Fail Update")
            .name("Name Fail Update")
            .email("")
            .birthday(LocalDate.parse("2030-07-05"))
            .build();
        try {
            userController.updateUser(userFailUpdate);
        } catch (ValidationException ignored) {
        }
        assertNotEquals(userValid, userFailUpdate, "Некорректное обновление userFailUpdate прошло валидацию");
    }
}