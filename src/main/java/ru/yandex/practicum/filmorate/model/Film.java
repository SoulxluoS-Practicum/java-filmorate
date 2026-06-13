package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Length(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull
    @Min(value = -1, message = "Продолжительность фильма должна быть положительным числом")
    Long duration;
}
