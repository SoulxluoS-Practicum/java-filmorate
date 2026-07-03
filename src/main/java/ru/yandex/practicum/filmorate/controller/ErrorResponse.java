package ru.yandex.practicum.filmorate.controller;

public record ErrorResponse(String error, String description) {
    public ErrorResponse(String error) {
        this(error, null);
    }

}