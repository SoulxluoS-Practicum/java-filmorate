package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> getById(long filmId);

    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

}
