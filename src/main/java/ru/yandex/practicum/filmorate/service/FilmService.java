package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    Comparator<Film> popularComparator = Comparator.comparingInt(o -> o.getUserLikeIds().size());

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    private final UserService userService;
    private final RatingService ratingService;

    public FilmDto create(FilmDto newFilm) {
        Film film = FilmMapper.mapToFilm(newFilm);

        if (newFilm.getMpa() != null) {
            Rating mpa = ratingService.findById(newFilm.getMpa().getId());
            film.setMpaId(mpa.getId());
        }

        if (newFilm.getGenres() != null) {
            Set<Long> genres = newFilm.getGenres().stream()
                    .map(Genre::getId)
                    .map(genreStorage::findById)
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            film.setGenres(genres);
        }
        Long id = filmStorage.create(film).getId();
        newFilm.setId(id);
        return newFilm;
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll()
                .stream()
                .map(FilmMapper::mapToDto)
                .peek(film -> {
                    List<Genre> genres = getGenresByFilmId(film.getId());
                    film.setGenres(genres);
                    if (film.getMpa() != null) {
                        Rating mpa = ratingService.findById(film.getMpa().getId());
                        film.setMpa(mpa);
                    }
                })
                .toList();
    }

    public FilmDto findById(Long filmId) {
        Film film = filmStorage.findById(filmId);
        FilmDto filmDto = FilmMapper.mapToDto(film);
        List<Genre> genres = getGenresByFilmId(filmId);
        filmDto.setGenres(genres);

        if (filmDto.getMpa() != null) {
            Rating mpa = ratingService.findById(filmDto.getMpa().getId());
            filmDto.setMpa(mpa);
        }
        return filmDto;
    }

    public FilmDto update(FilmDto updFilm) {
        if (updFilm.getId() == null) {
            throw new IdNotSpecifiedException();
        }
        Film targetFilm = filmStorage.findById(updFilm.getId());
        if (updFilm.getName() != null) {
            targetFilm.setName(updFilm.getName());
        }
        if (updFilm.getDescription() != null) {
            targetFilm.setDescription(updFilm.getDescription());
        }
        if (updFilm.getReleaseDate() != null) {
            targetFilm.setReleaseDate(updFilm.getReleaseDate());
        }
        if (updFilm.getDuration() != null) {
            targetFilm.setDuration(updFilm.getDuration());
        }
        if (updFilm.getMpa() != null) {
            Rating mpa = ratingService.findById(updFilm.getMpa().getId());
            targetFilm.setMpaId(mpa.getId());
        }
        if (updFilm.getGenres() != null) {
            Set<Long> genres = updFilm.getGenres().stream()
                    .map(Genre::getId)
                    .map(genreStorage::findById)
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            targetFilm.setGenres(genres);
        }
        return FilmMapper.mapToDto(update(targetFilm));
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(long filmId) {
        filmStorage.delete(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        User user = userService.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getUserLikeIds().add(user.getId());
        update(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userService.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getUserLikeIds().remove(user.getId());
        update(film);
    }

    public Collection<FilmDto> findPopular(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(popularComparator.reversed())
                .limit(count)
                .map(FilmMapper::mapToDto)
                .peek(dto -> dto.setGenres(getGenresByFilmId(dto.getId())))
                .toList();
    }

    private List<Genre> getGenresByFilmId(long filmId) {
        return genreStorage.findByFilmId(filmId)
                .stream()
                .toList();
    }
}
