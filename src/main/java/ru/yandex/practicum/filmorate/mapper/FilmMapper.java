package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        if (filmDto.getMpa() != null) {
            film.setMpaId(filmDto.getMpa().getId());
        }
        return film;
    }

    public static FilmDto mapToDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setLikes(new ArrayList<>(film.getUserLikeIds()));
        if (film.getMpaId() != null) {
            dto.setMpa(new Rating(film.getMpaId(), null, null));
        }
        return dto;
    }
}
