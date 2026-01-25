package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.annotation.AfterDate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.List;

/**
 * Film.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FilmDto {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @AfterDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Rating mpa;
    private List<Genre> genres;
    private List<Long> likes;
}
