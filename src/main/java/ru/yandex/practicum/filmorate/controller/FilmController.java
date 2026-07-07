package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private static final String FILM_LIKE_BY_USER_PATH = "/{id}/like/{userId}";
    private static final String POPULAR_PATH = "/popular";
    private final FilmService filmService;

    static {
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping("/{id}")
    public Optional<Film> getById(@PathVariable long id) {
        log.debug("Запрос на получение фильма по id = {}", id);
        return filmService.getById(id);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Запрос на получение списка фильмов");
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание нового фильма");
        film = filmService.create(film);
        log.info("Создан новый фильм(id = {}, name = {})", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Запрос на изменение данных фильма(id = {})", film.getId());
        film = filmService.update(film);
        log.info("Изменены данные фильма(id = {})", film.getId());
        return film;
    }

    @PutMapping(FILM_LIKE_BY_USER_PATH)
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Запрос на добавления лайка(film id = {}, userId = {})", id, userId);
        Film film = filmService.addLike(id, userId);
        log.debug("Добавлен лайк(film id = {}, userId = {})", id, userId);
        return film;
    }

    @DeleteMapping(FILM_LIKE_BY_USER_PATH)
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Запрос на удаление лайка(film id = {}, userId = {})", id, userId);
        Film film = filmService.removeLike(id, userId);
        log.debug("Удален лайк(film id = {}, userId = {})", id, userId);
        return film;
    }

    @GetMapping(POPULAR_PATH)
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрос на получение списка популярных фильмов(count = {})", count);
        return filmService.getPopular(count);
    }
}
