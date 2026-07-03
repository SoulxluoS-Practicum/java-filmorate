package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User
 */
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    @NotNull
    @Email(message = "Не корректная запись электронной почты")
    String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    String login;
    String name;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    Set<Long> friends;

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }
}
