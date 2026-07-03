package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmController filmController = new FilmController(filmStorage, new FilmService(filmStorage, userStorage));

    @Test
    void create() {
        Film filmEmpty = Film.builder().build();
        try {
            filmController.create(filmEmpty);
        } catch (RuntimeException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmEmpty), "Некорректная валидация filmEmpty: поля = null не должны проходить");

        Film filmValid = Film.builder()
            .name("filmValid")
            .description("D".repeat(200))
            .duration(100L)
            .releaseDate(LocalDate.parse("1985-12-28"))
            .build();
        filmController.create(filmValid);
        assertTrue(filmController.getAll().contains(filmValid), "Добавление корректного filmValid не прошло валидацию");

        Film filmFailName = filmValid.toBuilder().id(null).name("").build();
        try {
            filmController.create(filmFailName);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmFailName), "Некорректная валидация filmFailName: пустой name не должен проходить");

        Film filmFailDesc = filmValid.toBuilder().id(null).name("filmFailDesc").description("D".repeat(201)).build();
        try {
            filmController.create(filmFailDesc);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmFailDesc), "Некорректная валидация filmFailDesc: длина description > 200 не должна проходить");

        Film filmFailDur = filmValid.toBuilder().id(null).name("filmFailDur").duration(-4343L).build();
        try {
            filmController.create(filmFailDur);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmFailDur), "Некорректная валидация FilmFailDur: отрицательный duration не должен проходить");

        Film filmFailDate = filmValid.toBuilder().id(null).name("filmFailDate").releaseDate(LocalDate.parse("1895-12-27")).build();
        try {
            filmController.create(filmFailDate);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmFailDate), "Некорректная валидация FilmFailDate: releaseDate раньше 1895-12-28 не должен проходить");
    }

    @Test
    void update() {
        Film filmEmpty = Film.builder().build();
        try {
            filmController.update(filmEmpty);
        } catch (ValidationException | NotFoundException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmEmpty), "Пустой FilmEmpty не должен быть обновлен");

        Film filmFailId = Film.builder().id(-1L).name("FilmUnknown").build();
        try {
            filmController.update(filmFailId);
        } catch (ValidationException | NotFoundException ignored) {
        }
        assertFalse(filmController.getAll().contains(filmFailId), "FilmFailId с не корректным Id не должен быть обновлен");

        Film filmValid = Film.builder()
            .name("FilmValid")
            .description("D".repeat(200))
            .duration(100L)
            .releaseDate(LocalDate.parse("1895-12-28"))
            .build();
        filmController.create(filmValid);

        Film filmValidUpdate = filmValid.toBuilder()
            .name("FilmValidUpdate")
            .description("U".repeat(200))
            .duration(200L)
            .releaseDate(LocalDate.parse("2000-01-01"))
            .build();
        filmController.update(filmValidUpdate);
        assertEquals(filmValid, filmValidUpdate, "Корректное обновление filmValidUpdate не прошло валидацию");

        Film filmFailUpdate = filmValid.toBuilder()
            .name("FilmFailUpdate")
            .description("F".repeat(201))
            .duration(-333L)
            .releaseDate(LocalDate.parse("1800-01-01"))
            .build();
        try {
            filmController.update(filmFailUpdate);
        } catch (ValidationException | NotFoundException ignored) {
        }
        assertNotEquals(filmValid, filmFailUpdate, "Некорректное обновление filmFailUpdate прошло валидацию");
    }
}