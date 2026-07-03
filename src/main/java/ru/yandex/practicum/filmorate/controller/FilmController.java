package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;

    public FilmController(FilmStorage filmStorage) {
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Запрос на получение списка фильмов");
        return filmStorage.getFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма");
        film = filmStorage.createFilm(film);
        log.info("Создан новый фильм(id = {}, name = {})", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Запрос на изменение данных фильма(id = {})", film.getId());
        film = filmStorage.updateFilm(film);
        log.info("Изменены данные фильма(id = {})", film.getId());
        return film;
    }
}
