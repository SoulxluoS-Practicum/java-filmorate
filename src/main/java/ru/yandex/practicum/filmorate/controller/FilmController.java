package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmService = filmService;
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
        this.filmStorage = filmStorage;
    }

    @GetMapping("/{id}")
    public Optional<Film> getById(@PathVariable long id) {
        log.debug("Запрос на получение фильма по id = {}", id);
        return filmStorage.getById(id);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Запрос на получение списка фильмов");
        return filmStorage.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма");
        film = filmStorage.create(film);
        log.info("Создан новый фильм(id = {}, name = {})", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Запрос на изменение данных фильма(id = {})", film.getId());
        film = filmStorage.update(film);
        log.info("Изменены данные фильма(id = {})", film.getId());
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Запрос на добавления лайка(film id = {}, userId = {})", id, userId);
        Film film = filmService.addLike(id, userId);
        log.debug("Добавлен лайк(film id = {}, userId = {})", id, userId);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Запрос на удаление лайка(film id = {}, userId = {})", id, userId);
        Film film = filmService.removeLike(id, userId);
        log.debug("Удален лайк(film id = {}, userId = {})", id, userId);
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрос на получение списка популярных фильмов(count = {})", count);
        return filmService.getPopular(count);
    }
}
