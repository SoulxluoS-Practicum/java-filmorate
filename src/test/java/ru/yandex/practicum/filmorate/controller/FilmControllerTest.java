package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    @Test
    void createFilm() {
        Film filmEmpty = Film.builder().build();
        try {
            filmController.createFilm(filmEmpty);
        } catch (RuntimeException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmEmpty), "Некорректная валидация filmEmpty: поля = null не должны проходить");

        Film filmValid = Film.builder()
            .name("filmValid")
            .description("D".repeat(200))
            .duration(100L)
            .releaseDate(LocalDate.parse("1985-12-28"))
            .build();
        filmController.createFilm(filmValid);
        assertTrue(filmController.getFilms().contains(filmValid), "Добавление корректного filmValid не прошло валидацию");

        Film filmFailName = filmValid.toBuilder().id(null).name("").build();
        try {
            filmController.createFilm(filmFailName);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmFailName), "Некорректная валидация filmFailName: пустой name не должен проходить");

        Film filmFailDesc = filmValid.toBuilder().id(null).name("filmFailDesc").description("D".repeat(201)).build();
        try {
            filmController.createFilm(filmFailDesc);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmFailDesc), "Некорректная валидация filmFailDesc: длина description > 200 не должна проходить");

        Film filmFailDur = filmValid.toBuilder().id(null).name("filmFailDur").duration(-4343L).build();
        try {
            filmController.createFilm(filmFailDur);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmFailDur), "Некорректная валидация FilmFailDur: отрицательный duration не должен проходить");

        Film filmFailDate = filmValid.toBuilder().id(null).name("filmFailDate").releaseDate(LocalDate.parse("1985-12-27")).build();
        try {
            filmController.createFilm(filmFailDate);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmFailDate), "Некорректная валидация FilmFailDate: releaseDate раньше 1985-12-28 не должен проходить");
    }

    @Test
    void updateFilm() {
        Film filmEmpty = Film.builder().build();
        try {
            filmController.updateFilm(filmEmpty);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmEmpty), "Пустой FilmEmpty не должен быть обновлен");

        Film filmFailId = Film.builder().id(-1L).name("FilmUnknown").build();
        try {
            filmController.updateFilm(filmFailId);
        } catch (ValidationException ignored) {
        }
        assertFalse(filmController.getFilms().contains(filmFailId), "FilmFailId с не корректным Id не должен быть обновлен");

        Film filmValid = Film.builder()
            .name("FilmValid")
            .description("D".repeat(200))
            .duration(100L)
            .releaseDate(LocalDate.parse("1985-12-28"))
            .build();
        filmController.createFilm(filmValid);

        Film filmValidUpdate = filmValid.toBuilder()
            .name("FilmValidUpdate")
            .description("U".repeat(200))
            .duration(200L)
            .releaseDate(LocalDate.parse("2000-01-01"))
            .build();
        filmController.updateFilm(filmValidUpdate);
        assertEquals(filmValid, filmValidUpdate, "Корректное обновление filmValidUpdate не прошло валидацию");

        Film filmFailUpdate = filmValid.toBuilder()
            .name("FilmFailUpdate")
            .description("F".repeat(201))
            .duration(-333L)
            .releaseDate(LocalDate.parse("1900-01-01"))
            .build();
        try {
            filmController.updateFilm(filmFailUpdate);
        } catch (ValidationException ignored) {
        }
        assertNotEquals(filmValid, filmFailUpdate, "Некорректное обновление filmFailUpdate прошло валидацию");
    }
}