package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Optional<Film> getById(long filmId) {
        return filmStorage.getById(filmId);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Collection<Film> getPopular(int count) {
        return filmStorage.getAll().stream()
            .sorted(Comparator.comparing(film -> ((Film) film).getLikes().size()).reversed())
            .limit(count)
            .toList();
    }

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.getById(filmId)
            .orElseThrow(() -> NotFoundException.film(filmId));
        userStorage.getById(userId)
            .orElseThrow(() -> NotFoundException.user(userId));
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.getById(filmId)
            .orElseThrow(() -> NotFoundException.film(filmId));
        userStorage.getById(userId)
            .orElseThrow(() -> NotFoundException.user(userId));
        film.getLikes().remove(userId);
        return film;
    }

}
