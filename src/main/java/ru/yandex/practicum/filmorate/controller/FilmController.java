package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Level;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final Instant MIN_DATE = LocalDate.of(1985, 12, 28).atStartOfDay(ZoneOffset.UTC).toInstant();
    private static final int MAX_DESC_LENGTH = 200;
    private final Map<Long, Film> films = new HashMap<>();

    public FilmController() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Запрос на получение списка фильмов");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Запрос на создание нового фильма");
        validateName(film);
        validateDescription(film);
        validateReleaseDate(film);
        validateDuration(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм(id = {}, name = {})", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Запрос на изменение данных фильма(id = {})", film.getId());
        if (film.getId() == null) {
            throw new ValidationException("Id изменяемого фильма не указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
        }
        Film oldFilm = films.get(film.getId());
        if (film.getName() != null) {
            validateName(film);
            log.debug("Изменение name фильма(id = {}): {} -> {}", film.getId(), oldFilm.getName(), film.getName());
            oldFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            validateDescription(film);
            log.debug("Изменение description фильма(id = {}): {} -> {}", film.getId(), oldFilm.getDescription(), film.getDescription());
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            validateReleaseDate(film);
            log.debug("Изменение releaseDate фильма(id = {}): {} -> {}", film.getId(), oldFilm.getReleaseDate(), film.getReleaseDate());
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            validateDuration(film);
            log.debug("Изменение duration фильма(id = {}): {} -> {}", film.getId(), oldFilm.getDuration(), film.getDuration());
            oldFilm.setDuration(film.getDuration());
        }
        log.info("Изменены данные фильма(id = {})", oldFilm.getId());
        return oldFilm;
    }

    private void validateName(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым");
        }
    }

    private void validateDescription(Film film) {
        if (film.getDescription().length() > MAX_DESC_LENGTH) {
            throw new ValidationException("Описание фильма не должно быть длиннее " + MAX_DESC_LENGTH + " символов");
        }
    }

    private void validateReleaseDate(Film film) {
        if (parseReleaseDate(film.getReleaseDate()).isBefore(MIN_DATE)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше " + MIN_DATE);
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() < 1) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
            .stream()
            .mapToLong(id -> id)
            .max()
            .orElse(0);
        return ++currentMaxId;
    }

    private Instant parseReleaseDate(String releaseDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(releaseDate, formatter).atStartOfDay()
            .toInstant(ZoneOffset.UTC);
    }
}
