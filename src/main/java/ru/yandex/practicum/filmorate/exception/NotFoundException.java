package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

    public static NotFoundException user(long userId) {
        return new NotFoundException("Пользователь id = %s не найден".formatted(userId));
    }

    public static NotFoundException film(long filmId) {
        return new NotFoundException("Фильм id = %s не найден".formatted(filmId));
    }
}