package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody FilmDto newFilm) {
        return filmService.create(newFilm);
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public FilmDto findById(@PathVariable long filmId) {
        return filmService.findById(filmId);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto updFilm) {
        return filmService.update(updFilm);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable long filmId) {
        filmService.delete(filmId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }
}
