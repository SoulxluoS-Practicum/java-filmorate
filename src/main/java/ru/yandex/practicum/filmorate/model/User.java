package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * User
 */
@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private String birthday;
}
